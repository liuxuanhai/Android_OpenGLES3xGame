## 总结

### 墙壁

1. 由6个包含6个顶点卷绕的矩形

2. 墙壁光照可以只考虑环境光和散射光

   ```
   散射光照射结果 = 材质的反射系数(纹理图/白光下的颜色/rgb) * 散射光强度*max(cos(入射角),0)
   环境光照射结果 = 材质的反射系数(纹理图/白光下的颜色/rgb) * 环境光强度
   gl_FragColor = 散射光照射结果 + 环境光照射结果 
   
   或者,
   
   光照结果 =  材质的反射系数(纹理图/白光下的颜色/rgb)*（ 散射光强度(散射光颜色)*max(cos(入射角),0) + 环境光强度 ）
   ```

3. 需要计算光照的话，墙壁的正方向(法向量)要处理好，即旋转的方式要对

4. 由于纹理图是有方向的图片，所有从底部的一个矩形，旋转到左右前后墙壁的矩形时，要注意纹理图片方向




### 粒子系统

* 每个粒子的当前生命时间 ，可以通过顶点坐标(要传4个值,x,y,z,w)，最后的一个w，顺带传入渲染管线，shader中计算MVP的时候使用vec4(x,y,z,1.0)即可

* 也可以把顶点坐标(x,y,z,w)的z，作为当前粒子水平方向移动速度，案例中，根据每个粒子距离火炬中心的大小比例设置这个水平速度，每次更新x值，就通过 x=x+z 就得到

* 火焰是一个粒子，矩形的，所以要跟随摄像头旋转，通过旋转角度，来始终保持面向摄像头

* 当前粒子的衰减因子= 1 - 生命时间/最大生命

* ~~当前粒子的当前片元因子 = (1 - 中心距离/半径 ) * 衰减因子~~

* ~~中心距离可以用物体坐标系  distance(vPosition.xyz, vec3( 0., 0., 0.) )~~

* 修正：

  * 每个粒子，每次传入渲染管线，在物体坐标系坐标，都不是以vec3(0,0,0)为中心点的，而是有偏移xRange和yRange的，并且往x轴移动和往y轴正方向移动，所以要计算片元到粒子中心的距离，来计算衰减因子，是不能的，如果要做粒子中心颜色比较大 边缘比较浅色，直接修改__纹理贴图的alpha通道__
  * 也就是纹理贴图的alpha通道，既作为形状，也作为由里往外淡色
  * 案例做的效果是：计算当前片元/粒子， 与火盘中心点(物体坐标系坐 标是vec3(0,0,0))的距离，这样越远离火盘底部，火苗颜色越小

* 粒子的颜色由  初始颜色和终止颜色，根据因子，插值而成 colorT = clamp(factor4, endColor,startColor); 

* 粒子的纹理贴图 用 它的alpha通道来形成粒子形状 colorT = colorT * colorTL.a;

* 渲染每个粒子系统的时候，是关闭深度测试的，所有不同粒子系统(火炬)，要先经过排序，然后再渲染，保证alpha混合正常，排序通过计算不同__火炬/火盘的中心位置与摄像头之间的距离__

  ```
  	@Override
  	public int compareTo(ParticleSystem another) {// 重写的比较两个火焰离摄像机距离的方法
  
  		float xs=positionX-cx;
  		float zs=positionZ-cz;
  		
  		float xo=another.positionX-cx;
  		float zo=another.positionZ-cz;
  		
  		float disA = xs*xs + zs*zs ;
  		float disB = xo*xo + zo*zo;
  		return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;  
  	}
  ```

* 混合方式

  ```
  GL_FUNC_SUBTRACT                 ScSs - DcDs
  GL_FUNC_REVERSE_SUBTRACT  		 DcDs - ScSs
  ```

* GL_FUNC_ADD

  ```
   GL_FUNC_ADD 情况下 ,
   GL_ONE + GL_ONE 比 GL_SRC_ALPHA + GL_ONE 要明亮 
  ```

* 粒子的矩形 要面向摄像机，由于案例中4个粒子系统的坐标不一样(火炬盘)，所以要分别计算

  ```
  public void calculateBillboardDirection()	// 根据摄像机位置计算火焰朝向
  {
  	// 由于每个粒子系统都在不同位置，不是在世界坐标系中心，所有不能用MySurfaceView.direction
  	// 所以要根据每个火炬在世界坐标系中的位置，与摄像头的位置 得到角度
  	float xspan = positionX - sCameraX;
  	float zspan = positionZ - sCameraZ;
  	if(zspan<=0) {
  		yAngle=(float)Math.toDegrees(Math.atan(xspan/zspan));	
  	} else {
  		yAngle=180+(float)Math.toDegrees(Math.atan(xspan/zspan));
  	}
  }
  ```

  

### Matrix.setLookAtM

```
public static void setLookAtM(float[] rm, int rmOffset,
            float eyeX, float eyeY, float eyeZ,
            float centerX, float centerY, float centerZ, float upX, float upY,
            float upZ)
```

* 摄像头位置eye = (eyeX,eyeY,eyeZ)   目标位置center=(centerX,centerY,centerZ)  向上方向向量 up = (upX,upY,upZ)   
* 视线方向向量  = center-eye 箭头指向目标
* 视线和up方向向量 所在平面 由法向量表述：  视线方向向量 叉乘 up方向向量   右手螺旋
* 修正up方向向量，由于up方向向量与视线方向向量不垂直，所以再要 平面法向量  叉乘  视线方向向量
* 三个方向向量 就可以作为 新坐标的三个独立基向量  
  * 视线方向向量(取反方向)，作为 摄像机坐标系 z轴
  * 修正up方向向量，作为 摄像机坐标系 y轴
  * 平面法向量，作为 摄像机坐标系 x轴 （x轴 与 视线向量和up向量垂直 或者说垂直于摄像机器）





### Obj和mtl文件

[obj 和 mtl文件格式]: https://www.jianshu.com/p/b52e152d44a9
[mtl格式]: https://www.cnblogs.com/wiki3d/p/objfile.html

案例参照  obj_mtl_Example/ningning.max 导出的 ningning.obj和ningning.mtl 依赖贴图 ningning.tif

#### 注意

3dMax导出的纹理图坐标跟Android的上下镜像

已知三角形3个顶点可以根据与其他三角形顶点一样的顺序 v1-v2 v1-v3 然后叉乘这两个

#### obj 模型文件

__mtllib__ ningning.mtl  指定使用的模型文件

v 顶点坐标

vn 法向量

vt 纹理坐标

g  卷绕组/物体

usemtl  这组卷绕对应哪个材质

f 一个三角形卷绕索引(顶点坐标 纹理坐标 法向量)



#### mtl材质文件

```
newmtl mymtl_1
	# shininess of the material 反射系数 定义了反射高光度 反射指数值，该值越高则高光越密集，一般取值范围在0~1000
	# Ns 32
	# 指定材质表面的光密度，即折射值 
	# 可在0.001到10之间进行取值。若取值为1.0，光在通过物体的时候不发生弯曲。玻璃的折射率为1.5
	# Ni 1.500000
	# 参数factor表示物体融入背景的数量，取值范围为0.0~1.0
	# 取值为1.0表示完全不透明，取值为0.0时表示完全透明。
	# 当新创建一个物体时，该值默认为1.0，即无渐隐效果
	d  1.0000
	# 透明度(1-d)
	Tr 0.0000
	# 滤光投射率
	Tf 1.0000 1.0000 1.0000 
	# 材质的光照模型 可接0~10范围内的数字参数
	illum 2
	# 环境反射 ambient color
	Ka 0.050000 0.050000 0.050000
	# 漫反射 diffuse color
	Kd 0.500000 0.500000 0.500000
	# 镜反射 specular color
	# Ks 0.350000 0.350000 0.350000 
	# 为漫反射指定颜色纹理文件
	map_Ka brazier.jpg
	map_Kd brazier.jpg
```



