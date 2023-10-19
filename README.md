# Telegram的夜间白天切换效果组件



## 项目环境

>Compose Material3
>
>Kotlin 1.8.10
>
>Grade 8.4
>
>AGP 8.0.1

## 实现原理

### 思路

截图遮挡下层进行遮罩，下层切换好后上层使用paint进行混合绘制来显示下层，就达到了切换的效果

### 步骤

1. 获取当前页面截图。注(Compose获取截图时是通过LocalView进行获取，所以有节点树顺序影响，所以需要放在最外层)
2. 获取截图后绘制到页面上层遮住下方，绘制结束后走一个绘制结束回调进行主题切换。注(切换的时间一般是无感知的，所以不用关心同步等待问题)
3. 以页面对角线长度为半径使用Paint进行与截图图层的混合
4. 根据动画模式选择paint的模式是擦除还是相交显示
5. 使用ValueAnimator.ofFloat创建动画来进行圆的绘制，绘制结束后显示下层已经切换好的页面结束动画

## 兼容开发模式

> 注：动画的两个类型单独放在了constant文件中，MaskAnimModel

### Compose

> [组件源文件地址索引](https://github.com/setruth/MaskAnim/blob/master/app/src/main/java/com/setruth/themechange/components/MaskBox.kt)
>
> [使用文件地址索引](https://github.com/setruth/MaskAnim/blob/master/app/src/main/java/com/setruth/themechange/ui/screen/MaskBoxScreen.kt)

#### 使用方式

```kotlin
//MaskBox的基础布局是一个Box
MaskBox(
    animTime =1000L, //动画时间(毫秒为单位)
    maskComplete = {},//截图创建完成的回调，也就是动画准备完成开始直接的回调
    animFinish = { },//动画结束后的回调
) { maskAnimActiveEvent ->  
    Button(onClick = { 
        //调用函数激活动画，传入动画类型，动画扩展收缩的圆形的坐标，可以认为是点击坐标
        maskAnimActiveEvent(MaskAnimModel.EXPEND,0f,0f)
    }) {
        Text(text = "active mask anim")
    }
}
```

#### 效果图

<img src="./img/compose.gif" alt="效果图" style="zoom:25%;" />

### 原生XML

>正在迁移

