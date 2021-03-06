package com.bn.Sample11_2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

import static com.bn.Sample11_2.Constant.BETWEEN_GRASS_AND_ROCK;
import static com.bn.Sample11_2.Constant.CONFIG_TEXTRUE;
import static com.bn.Sample11_2.Constant.END_OF_GRASS;

public class Mountion
{
	//地形网格中每个小格子的尺寸
	float UNIT_SIZE=3.0f;
	
	//自定义渲染管线的id
	int mProgram;
	//总变化矩阵引用的id
	int muMVPMatrixHandle;
	//顶点位置属性引用id
	int maPositionHandle;
	//顶点纹理坐标属性引用id
	int maTexCoorHandle;

	// 顶点属性 纹理灰度图的坐标
	int maTexCoorHandle1;
	
	//草地的id
	int sTextureGrassHandle;
	//岩石纹理的引用
	int sTextureRockHandle;
	//过程纹理起始y坐标的引用
	int landStartYYHandle;
	//过程纹理跨度的引用
	int landYSpanHandle;
	//灰度图的id
	int sTextureLandHandle;

	//陆地的高度调整值
	int landHighAdjustHandle;
	//陆地最大高差
	int landHighestHandle;

	//顶点数据缓冲和纹理坐标数据缓冲
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	//灰度图纹理坐标
	FloatBuffer mTexCoorBuffer1;
	//地形中顶点的数量
	int vCount=0;


	public Mountion(MySurfaceView mv,float[][] yArray,int rows,int cols)
	{
		initVertexData(yArray,rows,cols);
		initShader(mv);
	}

	// 顶点着色器 通过每个顶点的灰度图坐标属性  在灰度图贴图texture中获取顶点的高度
	public Mountion(MySurfaceView mv,int rows,int cols)
	{
		initVertexData(rows,cols);
		initShader(mv);
	}

	//初始化顶点数据
    public void initVertexData(float[][] yArray,int rows,int cols)
    {
    	//顶点坐标数据的初始化
    	vCount=cols*rows*2*3;//每个格子两个三角形，每个三角形3个顶点
        float vertices[]=new float[vCount*3];//存储顶点x、y、z坐标的数组
        int count=0;//顶点计数器
        for(int j=0;j<rows;j++)//遍历地形网格的行
        {
        	for(int i=0;i<cols;i++) //遍历地形网格的列
        	{
        		//计算当前格子左上侧点坐标
//        		float zsx=-UNIT_SIZE*cols/2+i*UNIT_SIZE;
//        		float zsz=-UNIT_SIZE*rows/2+j*UNIT_SIZE;

				float zsx= (i-cols/2)* UNIT_SIZE; // 每个格子的大小是 UNIT_SIZE
        		float zsz= (j-rows/2)* UNIT_SIZE;

				//将当前行列对应的小格子中顶点坐标按照卷绕成两个三角形的顺序存入顶点坐标数组
        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j][i];
        		vertices[count++]=zsz;

        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j+1][i];
        		vertices[count++]=zsz+UNIT_SIZE;

        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j][i+1];
        		vertices[count++]=zsz;

        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j][i+1];
        		vertices[count++]=zsz;

        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j+1][i];
        		vertices[count++]=zsz+UNIT_SIZE;

        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j+1][i+1];
        		vertices[count++]=zsz+UNIT_SIZE;
        	}
        }

        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        //顶点纹理坐标数据的初始化
        float[] texCoor=generateTexCoor(cols,rows);
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);
    }

	public void initVertexData(int rows,int cols) {
		//顶点坐标数据的初始化
		vCount = cols * rows * 2 * 3;
		float vertices[] = new float[vCount * 3];
		int count = 0;//顶点计数器
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				//计算当前格子左上侧点坐标
				float zsx = -UNIT_SIZE * cols / 2 + i * UNIT_SIZE;
				float zsz = -UNIT_SIZE * rows / 2 + j * UNIT_SIZE;

				vertices[count++] = zsx;// 顶点坐标是没有归一化的
				vertices[count++] = 0;	// 高度会在顶点着色器中通过texture纹理采样得到
				vertices[count++] = zsz;

				vertices[count++] = zsx;
				vertices[count++] = 0;
				vertices[count++] = zsz + UNIT_SIZE;

				vertices[count++] = zsx + UNIT_SIZE;
				vertices[count++] = 0;
				vertices[count++] = zsz;

				vertices[count++] = zsx + UNIT_SIZE;
				vertices[count++] = 0;
				vertices[count++] = zsz;

				vertices[count++] = zsx;
				vertices[count++] = 0;
				vertices[count++] = zsz + UNIT_SIZE;

				vertices[count++] = zsx + UNIT_SIZE;
				vertices[count++] = 0;
				vertices[count++] = zsz + UNIT_SIZE;
			}
		}

		//创建顶点坐标数据缓冲
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
		mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);//设置缓冲区起始位置

		//顶点纹理坐标数据的初始化
		float[] texCoor=generateTexCoor(cols,rows,16.0f);
		ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
		cbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer = cbb.asFloatBuffer();
		mTexCoorBuffer.put(texCoor);
		mTexCoorBuffer.position(0);


		//灰度图顶点纹理坐标数据的初始化
		float[] texCoor_land=generateTexCoor(cols,rows,1.0f); // 这是归一化的 给到顶点着色器 作为每个顶点的属性
		ByteBuffer cbb1 = ByteBuffer.allocateDirect(texCoor_land.length*4);
		cbb1.order(ByteOrder.nativeOrder());
		mTexCoorBuffer1 = cbb1.asFloatBuffer();
		mTexCoorBuffer1.put(texCoor_land);
		mTexCoorBuffer1.position(0);

	}
	
	//初始化着色器的方法
	public void initShader(MySurfaceView mv) 
	{
		String mVertexShader;
		String mFragmentShader;
		switch (CONFIG_TEXTRUE){
			case One_Texture:
				mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
				mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
				break;
			case Using_Texture_In_VertexShader:
				mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_texture.sh", mv.getResources());
				mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_procedural.sh", mv.getResources());
				break;
			default:
				mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_procedural.sh", mv.getResources());
				mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_procedural.sh", mv.getResources());
				break;

		}


		//基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");


		if( CONFIG_TEXTRUE != Constant.RENDER_TYPE.One_Texture ){
			//草地
			sTextureGrassHandle=GLES30.glGetUniformLocation(mProgram, "sTextureGrass");
			//石头
			sTextureRockHandle=GLES30.glGetUniformLocation(mProgram, "sTextureRock");
			//x位置
			landStartYYHandle=GLES30.glGetUniformLocation(mProgram, "landStartY");
			//x最大
			landYSpanHandle=GLES30.glGetUniformLocation(mProgram, "landYSpan");

		}else{
			//草地纹理
			sTextureGrassHandle=GLES30.glGetUniformLocation(mProgram, "sTexture");
		}

		if(CONFIG_TEXTRUE == Constant.RENDER_TYPE.Using_Texture_In_VertexShader){
			//获取程序中灰度图顶点纹理坐标属性引用id
			maTexCoorHandle1= GLES30.glGetAttribLocation(mProgram, "aTexLandCoor");
			//灰度图
			sTextureLandHandle=GLES30.glGetUniformLocation(mProgram, "sTextureLand");
			//陆地的高度调整值
			landHighAdjustHandle=GLES30.glGetUniformLocation(mProgram, "landHighAdjust");
			//陆地最大高差
			landHighestHandle=GLES30.glGetUniformLocation(mProgram, "landHighest");
		}
	}

	public void drawSelf(int texId,int rock_textId, int land_texId)
	{
		//指定使用某套着色器程序
   	 	GLES30.glUseProgram(mProgram); 
        //将最终变换矩阵送入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将顶点位置数据送入渲染管线
		GLES30.glVertexAttribPointer
		(
			maPositionHandle,
			3,
			GLES30.GL_FLOAT,
			false,
			3*4,
			mVertexBuffer
		);
		//将纹理坐标数据送入渲染管线
		GLES30.glVertexAttribPointer
		(
			maTexCoorHandle,
			2,
			GLES30.GL_FLOAT,
			false,
			2*4,
			mTexCoorBuffer
		);
		//启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);  
        //启用纹理坐标数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);
        
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
		GLES30.glUniform1i(sTextureGrassHandle, 0);//使用0号纹理

		if( CONFIG_TEXTRUE != Constant.RENDER_TYPE.One_Texture ) {
			GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, rock_textId);//绑定岩石纹理
			GLES30.glUniform1i(sTextureRockHandle, 1); //岩石纹理编号为1

			GLES30.glUniform1f(landStartYYHandle, END_OF_GRASS);//传送过程纹理起始y坐标
			GLES30.glUniform1f(landYSpanHandle, BETWEEN_GRASS_AND_ROCK );//传送过程纹理跨度
		}

		if(CONFIG_TEXTRUE == Constant.RENDER_TYPE.Using_Texture_In_VertexShader){
			GLES30.glVertexAttribPointer ( maTexCoorHandle1,
							2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer1 );
			GLES30.glEnableVertexAttribArray(maTexCoorHandle1);

			GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, land_texId);
			GLES30.glUniform1i(sTextureLandHandle, 2);

			GLES30.glUniform1f(landHighAdjustHandle, Constant.LAND_HIGH_ADJUST);
			GLES30.glUniform1f(landHighestHandle, Constant.LAND_HIGHEST );
		}


		//绘制纹理矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);

	}
	//自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2];
    	float sizew=16.0f/bw;//列数 16.0 分别代表S/T轴的最大坐标值 这样意味着纹理在整个地形中重复了16次
    	float sizeh=16.0f/bh;//行数
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s=j*sizew;
    			float t=i*sizeh;

    			result[c++]=s;
    			result[c++]=t;

    			result[c++]=s;
    			result[c++]=t+sizeh;

    			result[c++]=s+sizew;
    			result[c++]=t;

    			result[c++]=s+sizew;
    			result[c++]=t;

    			result[c++]=s;
    			result[c++]=t+sizeh;

    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;
    		}
    	}
    	return result;
    }


	public float[] generateTexCoor(int bw,int bh,float size)
	{
		float[] result=new float[bw*bh*6*2];
		float sizew=size/bw;//列数
		float sizeh=size/bh;//行数
		int c=0;
		for(int i=0;i<bh;i++)
		{
			for(int j=0;j<bw;j++)
			{
				//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
				float s=j*sizew;
				float t=i*sizeh;

				result[c++]=s;
				result[c++]=t;

				result[c++]=s;
				result[c++]=t+sizeh;

				result[c++]=s+sizew;
				result[c++]=t;

				result[c++]=s+sizew;
				result[c++]=t;

				result[c++]=s;
				result[c++]=t+sizeh;

				result[c++]=s+sizew;
				result[c++]=t+sizeh;
			}
		}
		return result;
	}
}