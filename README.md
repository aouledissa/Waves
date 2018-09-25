![](https://jitpack.io/v/andromedcodes/Waves.svg)
# [Waves](https://github.com/andromedcodes/Waves) - Yet A Better Shimmer Api (YABS)

Waves is simply a light weight library that adds shimmer effect to views in Android.
[Waves](https://github.com/andromedcodes/Waves) is pretty much straight forward API that was built on a **KISS (Keep It Stupid Simple)** fashion. Thanks to is powerful design, [Waves](https://github.com/andromedcodes/Waves)
provides a better, yet much easier, way to implement a shimmer effect on any desired view on Android. It also designed to work in two different fashions, **Automatically** by adding shimmer to all views,
or **Manually** by specifying which views, colors, shimmer speed and many other configurations.

<div style="text-align:center">
<img src="./images/Waves-demo.gif" align="center" alt="demo" width="200"/>
</div>

# Setup

**1. Add jitpack to your root build.gradle at the end of repositories** 

_You do not have to you do this if you already have the same config for other `Jitpack Libraries`_
``` groovy
allprojects {
  repositories {
	  ...
		maven { url 'https://jitpack.io' }
	}
}
``` 
**2. Add Waves dependency to your app gradle file**

On your app module gradle file, add Waves dependency inside the `dependencies` scope.
```groovy
dependencies {
  implementation 'com.github.andromedcodes:Waves:{latest-version}' // latest version on the title
}
```
# How to use Waves (Manual Fashion)

[Waves](https://github.com/andromedcodes/Waves) is designed to work in two different fashions, **Automatically** by adding shimmer to all views,
or **Manually** by specifying which views, colors, shimmer speed and many other configurations.

**1. Adding views manually**

Once [Waves](https://github.com/andromedcodes/Waves) is setup, you can start to add views you want to have shimmer effect on. Use the provided Builder method `on(View view)` to tell [Waves](https://github.com/andromedcodes/Waves) which views to apply shimmer effect on. End the `RequestBuilder` with `start()` to start the shimmer effect.
``` java
Waves.on(sampleTitle) // sampleTitle view reference
     .on(findViewById(R.id.sampleImg)) // sampleImg view reference
     .start(); // start shimmer effect on the previous views
```

By default, Waves automatically applys a foreground layer on each view with a default gray color `#D3D3D3` and a moview shade with a default color `#E3E3E3`.

**2. Customize shimmer colors**

If you don't like the shimmer default colors (`#D3D3D3` | `#E3E3E3`), [Waves](https://github.com/andromedcodes/Waves) offers you the possibility to style your shimmer colors.

**2.1 Customize shimmer color (animation color)**

To specify the shimmer color, use the provided builder method `waveColor(@ColorRes int color)`.
```java
Waves.on(sampleTitle) // sampleTitle view reference
     .waveColor(R.id.my_custom_shimmer_color) // you can also pass a HEX color 0xFFE3E3E3
     .start(); // start shimmer effect on the previous views
```
**2.2 Customize shimmer backgournd color (background color)**

To specify the views background color, use the provided builder method `backgroundColor(@ColorRes int color)`. By doing so, [Waves](https://github.com/andromedcodes/Waves) will add
foreground layer on top of each assigned view.
```java
Waves.on(sampleTitle) // sampleTitle view reference
     .backgroundColor(R.id.my_custom_background_color) // you can also pass a HEX color 0xFFD3D3D3
     .start(); // start shimmer effect on the previous views
```

**2.3 Add a color set**

For more flexibility, [Waves](https://github.com/andromedcodes/Waves) provides a simpler and faster way to reduce boilerplate code when specifying shimmer colors config.
In fact, you can directly use the provided builder method `waveColorSet(int[] colors)` to set all the colors config at once. This could be useful if you want to have
a gradient fashion shimmer.
``` java
int[] colors = {0xFFD3D3D3, 0xFFE3E3E3, 0xFFD3D3D3};

Waves.on(sampleTitle)
     .waveColorSet(colors)
     .start();
```

Another way to achieve the same goal is by using the provided builder method `waveColorSet(@ColorRes int backgroundColor, @ColorRes int waveColor)`
```java
Waves.on(sampleTitle)
     .waveColorSet(R.id.my_custom_background_color, R.id.my_custom_shimmer_color)
     .start();
```

**3. Control the shimmer animation speed**

[Waves](https://github.com/andromedcodes/Waves) provides the possibility to control the shimmer animation speed. In order to achieve a faster or slower shimmer effect
according to your design requirement, you can use the provided builder method `speed(long speed)` to control the speed. The parameter passed to this method is the time
duration in milliseconds.
``` java
Waves.on(sampleTitle)
     .speed(1000) // one second duration
     .start();
```

**4. Shimmer stop configuration**

[Waves](https://github.com/andromedcodes/Waves) is designed to listen automatically to data changes on views, so once shimmer animation has started and as soon as
 the data are fed to the views, [Waves](https://github.com/andromedcodes/Waves) will automatically dismiss the shimmer (based on your dismissing flavour). this behaviour
 allows you to only care about when you should start the animation and leave the rest for the API.

**4.1 Stop all shimmers at once**

<div style="text-align:center">
<img src="./images/Waves-demo-2.gif" align="center" alt="demo-2" width="200"/>
</div>

[Waves](https://github.com/andromedcodes/Waves) provides a configuration to dismiss all shimmers from all views at once. In another words, once a view had received content,
if `stopAllAtOnce` config is set to true, all the views will dismiss their shimmers. To enable this feature, use the provided builder method `stopAllAtOnce(boolean enabled)`

```java
Waves.on(sampleTitle)
     .stopAllAtOnce(true) // all shimmers will be dismissed once any view will receive content
     .start();
```

> By default this feature is set to <span style="color:red;">`false`</span>.

**4.2 Stop with leader view**

[Waves](https://github.com/andromedcodes/Waves) provides another way to configure your dismissing behaviour. You can specify a `leader view` to control the shimmer collection.
To do so you can use the provided builder method `leader(View view)`.

``` java
Waves.on(sampleTitle)
     .on(sampleImg)
     .leader(sampleImg) // the leader view
     .start();
```

# How to use Waves (Automatic Fashion)
> _<span style="color:red;">This feature is still in alpha release, use it carefully!!_</span>

If you are a super developer who care about code cleaning, taking off the boilerplate and keeping all the code in a good shape, then this **bonus feature** is right for you!!
[Waves](https://github.com/andromedcodes/Waves) has a more concise and ultra super easy way to do all the previous configs just in one line. In fact, if your view is
flat (only one wrapper root layout) you can use the builder method `apply(Activity context, @ColorRes int color, long duration, boolean stopAllAtOnce)`.
``` java
Waves.apply(this, R.color.colorPrimary, 1000, false);
```

Just as simple as that and leave the rest to [Waves](https://github.com/andromedcodes/Waves) it will handle everything for you.

# Contribution

A kind request to all [Waves](https://github.com/andromedcodes/Waves) users, if you like this library and you would like to
contribute or change something in it, please fork this project instead of downloading the source code. Thank your in advance.

# Author

[Mohamed Aouled Issa - Android Developer](https://www.linkedin.com/in/mohammed-aouled-issa)
