### 欧拉角
* 欧拉角 姿态角
* 欧拉角是表示__两个坐标系相对取向关系__的一种__表示方法__，当然还有其他方法( 旋转可以有很多种表示：__欧拉角__、__四元数__、__旋转矩阵__、旋转矢量 )
* 对于在三维空间里的一个参考系，任何坐标系的取向，都可以用三个欧拉角来表现(参考系即为大地参考系，是静止不动的；坐标系则固定于四轴飞行器，随着四轴飞行器的旋转而旋转)
* 欧拉角使用roll，pitch，yaw来表示这些分量的旋转值。需要注意的是，这里的旋转是针对大地参考系说的，这意味着第一次的旋转不会影响第二、三次的转轴(还是绕原来的坐标系轴)，因为欧拉描述中针对x,y,z的‘旋转描述’是世界坐标系下的值，所以当任意一轴旋转90°(绕其他的轴)的时候会导致该轴同其他轴(旋转之前的)重合，此时旋转被重合的轴可能没有任何效果(roll,pitch,yaw是参考旋转之前的坐标系，在这一组旋转中，参考坐标系都是旋转之前的，可以认为就是大地参考系)，这就是Gimbal Lock
* 对于一个物体的姿态，需要提供"参考系"和"顺规"，这样才可根据欧拉角，知道物体姿势，得到物体坐标系
* 参考系，一般是实验室参考系或者大地坐标系，静止不动的
* 机体坐标系与大地参考系之间的夹角关系：欧拉角
* 顺规
  * 由于先绕某个轴旋转，再绕着另外一个轴旋转，跟倒过来，先绕另外一个轴旋转，再绕某个轴旋转，会导致结果不一样，所以要定义顺规，这个给定的一组欧拉角，就可在同一个参考系内，得到同一个结果
  * 在一样的参考坐标系(比如，大地坐标系)，不同顺规(比如，顺规是zxz和zxy)，对于同样的一组欧拉角(roll,pitch,yaw)，按不同的顺规旋转后的结果是不一样的
  * 一般是zxz(wiki 经典力学 x顺规)和zxy(Unity,航空航天工程学)
  * 一共有12种顺规(3x2x2，规定:任何两个连续的旋转，必须绕着不同的转动轴旋转)
  * ~~zxz就是，先在参考坐标系，绕z轴旋转，然后绕着'旋转后的x轴'在旋转，最后绕着‘旋转后的z轴’再旋转~~
  * 对于夹角的顺序和标记，夹角的两个轴的指定，并没有任何定义，科学家对此从未达成共识，此外，实际上，还有许多其他方法，可以设定__两个坐标系的相对取向__，‘欧拉角方法’只是其中的一种。
  * Unity中的欧拉旋转，是指定一组欧拉旋转(90,60,30)，通过前述的顺规我们知道，先绕Z轴旋转30度，再绕X轴旋转90度，再绕Y轴旋转60度，虽然有这样的顺序，但是Z旋转后相对X轴、Y轴，都是__执行本组欧拉旋转前的那个轴向__，它没有发生变动，也就是Transform.Rotate(new Vector3(90,60,30))，是参考变化之前的参考系(大地坐标系)
  * Unity中的欧拉旋转。它是沿着Z、X、Y顺规执行的旋转，一组欧拉旋转过程中，相对的轴向不会发生变化。Transform.Rotate(new Vector3(90,60,30))，代表执行了一组欧拉旋转，它__相对的是旋转前的局部坐标朝向__
  * 插值，比如，[欧拉角与万向节死锁](https://blog.csdn.net/AndrewFan/article/details/60981437) 文章中的，Rotate(90,0,1) Rotate(90,0,2) Rotate(90,0,3).....Rotate(90,0,N) ， 每一次Rotate都是机体从原水平位置，根据先z轴转N度，然后绕x轴转90度，最终的结果显示出来

![yaw_roll_pitch](yaw_roll_pitch.png)

* 机体坐标系(Aircraft-body coordinate frame)
  * 原点O取在飞机质心处，机体坐标系与飞机固连
  * x轴 在飞机对称平面内，并平行于飞机的设计轴线指向机头
  * y轴 垂直于飞机对称平面指向机身右方
  * z轴 在飞机对称平面内，与x轴垂直并指向机身下方

* 地面坐标系（earth-surface inertial reference frame)
  * 在地面上选一点Og
  * xg轴 在水平面内并指向某一方向(比如北极方向)
  * yg轴 在水平面内垂直于xg轴，~~其指向按右手定则确定~~(这里应该是左手)
  * zg轴 垂直于地面并指向地心
  * 可以认为是 在水平地面上，正北方为xg轴正方向，正东方位yg轴正方向，重力垂直向下是zg轴正方向

* 姿态角均为零时，两个坐标系相应坐标轴重合

* 飞机的三个姿态角

  * 俯仰角θ
    * 机体坐标系X轴与水平面的夹角。当X轴的正半轴位于过坐标原点的水平面之上（抬头）时，俯仰角为正，否则为负。
  * 偏航角Ψ
    * 机体坐标系xb轴在水平面上投影与地面坐标系xg轴（在水平面上，指向目标为正）之间的夹角，由xg轴逆时针转至机体xb的投影线时，偏航角为正，即机头右偏航为正，反之为负。
  * 滚转角φ
    * 滚转角（又称为侧滚角）是指机体坐标系zb轴与通过机体xb轴的铅垂面间的夹角，机体向右滚为正，反之为负。

  


### Android平台获得欧拉角
* 加速度传感器三轴+磁场传感器三轴，总共六轴联算
* 两个API，一个获取旋转矩阵，一个获取欧拉角: 
```
  // gravity geomagnetic 分别是 加速度和磁场传感器 的三轴值
  // I 可以为空
  // R 用来转载返回值：旋转矩阵R
  public static boolean getRotationMatrix(float[] R, float[] I,
            float[] gravity, float[] geomagnetic)
  
  // R 旋转矩阵
  // values 用来装载返回值 欧拉角
  public static float[] getOrientation(float[] R, float values[]) 
  
  values[0]: azimuth, rotation around the Z axis.  绕z轴旋转的 航向角
  values[1]: pitch, rotation around the X axis.    绕x轴旋转的 俯仰角
  values[2]: roll, rotation around the Y axis.     绕y轴旋转的 滚筒角

```

* tan2

  * atan2(a, b)与atan(a/b)稍有不同
  * atan2(a,b)的取值范围介于-pi到pi之间(不包括-pi): 这个得到的弧度，考虑了原来坐标点所在的四个像限
  * atan(a/b)的取值范围介于-pi/2到pi/2之间(不包括±pi/2)

  

### getRotationMatrix 原理

* [官网说明](https://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix)

* 加速度和磁场传感器，返回的值，都是参照一样的坐标系(手机机体/物体/自身坐标系)，以手机左下角为原点，垂直平面向上为z轴正方向，符合右手准则
* 因此，加速度和磁场传感器，返回的值，可看成，这个物体坐标系上的两个向量，一个垂直水平面~~向下~~向上(__垂直向量__，重力的反方向，放在水平桌面的值是(0,0,9.8) )，一个指向北极方向(__北极向量__，但未必跟重力向量垂直)
* 这两个向量叉乘(代码实现中是磁场叉加速度)，得到水平东西方向的向量，按照右手准则，指向东方(__东方向量__)
* 由于之前的垂直向量和北极向量不一定相互垂直，所以，再 垂直向量 叉 东方向量，得到__最终的北极向量__(这个北极向量跟垂直向量 一定相互垂直)
* 最终，__北极向量，东方向量，垂直向量__，三个__基向量__构成__大地坐标系__ 在 __物体坐标系__中 的 位置
* 大地坐标系的这三个基向量组成的矩阵，自然也就是__旋转矩阵__。和__三维旋转矩阵__推导出的__含有航向角（方向角）、倾斜角、旋转角的旋转矩阵__一一对应，就可以求出相应的角度。
* 注意：实现中返回的矩阵是 跟OpenGL一样，按列为主的，R[0],R[1],R[2] 是作为一列(东方向量)
* 旋转变换矩阵
  * 旋转变换矩阵(线性变换) 用原来的坐标系表示，那么右边的向量可以是：
    * 一个点用原坐标系表示，得到结果是  这个点 在变换后 在原坐标系的位置
    * 一个点用变换后坐标系表示，得到结果是  这个点 在原坐标系上的位置
  * 这个__变换矩阵__是用物体坐标系表示的，变换成大地坐标系，所以可以右乘某个点在大地坐标系表示的位置向量 ，得到这个点 在 手机物体坐标系上表示 的 位置向量
* 这个__'旋转'的变换矩阵__ ，可以通过下面的getOrientation原理，多次右乘一个围绕某个轴矩阵得到



### getOrientation的原理

* 参考文章 [三维旋转矩阵推导](https://blog.csdn.net/yhl_leo/article/details/50732966)

* Yaw角(Azimuth)，偏航角， 0度是正北方，90度(1.57rad)是正东方，-90度(-1.57rad)是正西方 [-π~π]

* Pitch角，俯仰角， 上翘为负   [-π/2 ~ π/2]

* Roll角，右倾斜为正(右侧高时为负) ，[-π ~ π]

* 大地坐标系，北为正，东为正，重力方向反方向(即天空方向)为正

* 注意推导过程中参考轴是变化的！按照 顺规ZXY，__每次旋转，都按照上一次旋转后的新物体自身坐标系，来旋转__

* 推导旋转矩阵结果是：( φ是滚筒角 values[2],  ω是俯仰角values[1]  , κ是航向角 values[0] , 下面的推导矩阵是列主序, 列向量,放在矩阵右边,  合成的R 可以看成是__大地坐标系表示的旋转变换矩阵__ ,上面的三个基向量是__物体坐标系表示的旋转变换矩阵__)，这旋转过程中的__三个矩阵都是按旋转前的参考系的三个轴向，即大地坐标系__

  ![推导的旋转矩阵](推导的旋转矩阵.png)

  ![旋转矩阵与欧拉角](旋转矩阵与欧拉角.png)

* 旋转矩阵是正交矩阵

  * 正交矩阵每一列都是单位矩阵，并且两两正交
  * 正交矩阵的逆（inverse）等于正交矩阵的转置（transpose）
  * 正交矩阵的行列式的值肯定为正负1的

* getRotationMatrix得到的矩阵的三个列向量，作为北极向量，东方向量，垂直向量，代表大地坐标系三个维度的基向量，可以通过这个矩阵，把大地坐标系的某个点的坐标位置，换算成手机物体坐标系上的表示

* 而从上面一节，注意 : 得到的矩阵 R[0] R[1] R[2] 是第一列 (我们获得的传感器数据其实是__行主序__的，右边是向量 坐标是这个矩阵 )

* 由于OpenGL会认为R[0] R[1] R[2] 作为第一列，所以矩阵不用转置就可以给到OpenGL

* 由于旋转矩阵是正交矩阵，所以其逆矩阵就是转置矩阵，从大地坐标系到物体坐标系，就是getRotaionMatrix返回矩阵的转置，就是用大地坐标系表示的旋转矩阵

* ??? 这样有点没有对上???

  ![R](R.png)

  ![value](values.png)

  ```
  values[0]: azimuth, rotation around the Z axis.  绕z轴旋转的 航向角
  values[1]: pitch, rotation around the X axis.    绕x轴旋转的 俯仰角
  values[2]: roll, rotation around the Y axis.     绕y轴旋转的 滚筒角
  ```

  


### 欧拉角的万向节死锁
* 参考文章 [欧拉角与万向节死锁](https://blog.csdn.net/AndrewFan/article/details/60981437)   
* 平衡环架 ( 万向节 Gimbal)
    * 目标是保证中间的"转子和转轴",转子要保持水平，中间的旋转轴要保持垂直
    * 从外面到里面来看，每个平衡环到里面的平衡环的连接头，就是自己的旋转轴，一共有三个连接头，提供3个自由度(偏航 俯仰 旋转/桶滚)
    * 万向锁的结果是，有两个连接头/旋转轴，的转动都是改变同一个自由度，所以虽然可以指定三个角，但是只会有两个方向上的改变
    * 只要当一个轴先旋转90度，然后再其他轴做旋转，就会出现
* 欧拉角的“万向节死锁”问题，是__由于欧拉旋转定义本身造成的__，欧拉角顺规和轴向的定义方式，造就了“万向节死锁”问题的自然形成
    * 一个轴的转动会影响另外一个轴，所以后面的轴要旋转，必然受到之前的轴的影响
    * 物体旋转了一次之后(比如z轴)，其自身的坐标系已经跟大地坐标系不一样了，如果绕z轴旋转了90度，之后的自身坐标系就会另外有一个轴，与
* <https://blog.csdn.net/andrewfan/article/details/60866636>
* public void Rotate(Vector3 eulerAngles, Space relativeTo = Space.Self);
* Space.Self 局部坐标系，意味着本次欧拉旋转以物体当前的局部坐标朝向为基础出发执行旋转。
* Space.World 世界坐标系，意味着本次欧拉旋转以物体当前的世界坐标朝向为基础出发执行旋转。 
* 最重要的倒不是它有可选的世界坐标系，一般而言，常用的旋转都是相对当前局部坐标系执行的
* 最重要的是：在本次欧拉旋转过程中，它的相对轴是始终不变的，不变的，不变的
* 比如我们可以指定一组欧拉旋转(90,60,30)，通过前述的顺规我们知道，先绕Z轴旋转30度，再绕X轴旋转90度，再绕Y轴旋转60度
* 虽然有这样的顺序，但是Z旋转后相对X轴、Y轴，都是执行本组欧拉旋转前的那个轴向，它没有发生变动，所以我称它为“当前轴”。
* 在Unity中的欧拉旋转就是这样定义的，不排除其它学术中欧拉旋转有不一样的定义方式。因此，执行：
    * Transform.Rotate(new Vector3(90,60,30))
* 和执行
    * Transform.Rotate(new Vector3(0,0,30))；
    * Transform.Rotate(new Vector3(90,0,0));
    * Transform.Rotate(new Vector3(0,60,0))；
* 的结果是不一样的。
* 第一种情况，只执行了一组欧拉旋转
* 第二种情况，执行了三组欧拉旋转，后两组欧拉旋转的相对轴在旋转时已经发生了变动
* 
* 上面万向节平衡环就是第二种情况
* 以极坐标不一样，__极坐标__强调是__一个点的位置__，所以__可用三个角唯一表示__，但不是一个点的，而是有__取向__的一个坐标系(立体物体)，用欧拉角的方法表示，会导致__不是一一映射__(即使按照顺规，同样一个方向位置，可以有两种欧拉角来表示，如上面例子(90,180,180)和(90,0,0)是同样的效果 )

### Demo
* 在高通晓龙820,小米5上跑是正常的(如上面yaw,pitch,roll所述)，但在荣耀v10是不正确的，原因不清楚

### 其他传感器
* 加速度传感器: 左下角 右手准侧  m²/s    F合 = F外 + G    a合 = a外 + g    a外 = a合 - g (g是向量 方向向下) 如果水平放在桌面，就是 a外 = 0 - (-g)   这里g是标量=9.8 所以a外=9.8 向上为正，比如手机放在水平桌面，结果是(0,  0, 9.8) , 注意最后一个是正的
* 磁场传感器：左下角 右手准侧  微特斯拉
* 陀螺仪传感器：左下角 右手准侧 rad/s 
* 接近传感器：1cm左右，离散值分别表示接近和非接近
* 光传感器：返回光强度，流明单位 
* 温度传感器：返回摄氏度




### 四元数（Quaternion）

* 四元数比欧拉角多了一个维度，不容易直观的理解
* 三维旋转就是四维旋转的一个子集，因此，用四元数可以很方便的表示三维旋转
* 优点
  * 可以避免万向节锁
  * 往往效率更高
  * 可以提供平滑插值