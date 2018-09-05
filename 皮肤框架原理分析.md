# Android 换肤

## 常用方法
### 1.通过Theme切换主题
通过在setContentView之前设置Theme实现主题切换。
在styles.xml定义一个夜间主题和白天主题：

    <style name="LightTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <!--主题背景-->
        <item name="backgroundTheme">@color/white</item>
    </style>

    <style name="BlackTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <!--主题背景-->
        <item name="backgroundTheme">@color/dark</item>
    </style>

设置主要切换主题View的背景：

	<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:background="?attr/backgroundTheme"
	    tools:context=".MainActivity">
	
	    <Button
	        android:id="@+id/btn"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:text="切换主题"
	        app:layout_constraintBottom_toBottomOf="parent"
	        app:layout_constraintLeft_toLeftOf="parent"
	        app:layout_constraintRight_toRightOf="parent"
	        app:layout_constraintTop_toTopOf="parent" />
	
	</android.support.constraint.ConstraintLayout>

切换主题：
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setTheme(R.style.BlackTheme);
	    setContentView(R.layout.activity_main);
    }
	
	
	finish();
	Intent intent = getIntent();
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	startActivity(intent);
	overridePendingTransition(0, 0);

### 2.通过AssetManager切换主题
下载皮肤包，通过AssetManager加载皮肤包里面的资源文件，实现资源替换。
## Android-Skin-Loader
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/pic1.png)
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/pic2.png)
### ClassLoader
Android可以通过classloader获取已安装apk或者未安装apk、dex、jar的context对象，从而通过反射去获取Class、资源文件等。

#### 加载已安装应用的资源

![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/pic3.png)

	//获取已安装app的context对象
	Context context = ctx.getApplicationContext().createPackageContext("com.noob.resourcesapp", 		Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
	//获取已安装app的resources对象
	Resources resources = context.getResources();
	//通过resources获取classloader，反射获取R.class
	Class aClass = context.getClassLoader().loadClass("com.noob.resourcesapp.R$drawable");
	int resId = (int) aClass.getField("icon_collect").get(null);
	imageView.setImageDrawable(resources.getDrawable(id));

#### 加载未安装应用的资源
	
	String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.apk";
	//通过反射获取未安装apk的AssetManager
	AssetManager assetManager = AssetManager.class.newInstance();
	//通过反射增加资源路径
	Method method = assetManager.getClass().getMethod("addAssetPath", String.class);
	method.invoke(assetManager, apkPath);
	File dexDir = ctx.getDir("dex", Context.MODE_PRIVATE);
	if (!dexDir.exists()) {
	    dexDir.mkdir();
	}
	//获取未安装apk的Resources
	Resources resources = new Resources(assetManager, ctx.getResources().getDisplayMetrics(),
	        ctx.getResources().getConfiguration());
	//获取未安装apk的ClassLoader
	ClassLoader classLoader = new DexClassLoader(apkPath, dexDir.getAbsolutePath(), null, ctx.getClassLoader());
	//反射获取class
   	Class aClass = classLoader.loadClass("com.noob.resourcesapp.R$drawable");
   	int id = (int) aClass.getField("icon_collect").get(null);
   	imageView.setImageDrawable(resources.getDrawable(id));

### LayoutInflater.Factory
#### 分析setContentView源码

LayoutInflater.Factory是如何被调用的
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/source1.png)
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/source2.png)
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/source3.png)
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/source4.png)

测试代码：

	LayoutInflater.from(this).setFactory(new LayoutInflater.Factory() {
            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                Log.e("MainActivity", "name :" + name);
                int count = attrs.getAttributeCount();
                for (int i = 0; i < count; i++) {
                    Log.e("MainActivity", "AttributeName :" + attrs.getAttributeName(i) + "AttributeValue :"+ attrs.getAttributeValue(i));
                }
                return null;
            }
   	});
   	
   	
log日志：
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/loginfo.png)

### Android-Skin-Loader源码分析

#### 关键方法

分别是添加和删除View的观察者的接口，以及一个更新的方法
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/main1.png)
notifySkinUpdate具体逻辑
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/main2.png)
#### 如何实现立即换肤
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/factory1.png)
#### 如何开启新的Activity直接换肤换肤
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/factory2.png)
#### 如何获取皮肤包资源
SkinManager的初始化方法获得了AssetManager，从而获取了皮肤包的Resource对象

	SkinManager.getInstance().loadSkin();


	@SuppressLint("StaticFieldLeak")
    private void load(SkinTheme theme, final ILoaderListener callback) {

        new AsyncTask<SkinTheme, Void, Resources>() {

            private boolean isNeedCopy(SkinTheme newsTheme) {
                SkinTheme cacheTheme = SkinUtil.getTheme(context, newsTheme.name);
                return SkinUtil.isNewsTheme(newsTheme, cacheTheme) || debug;
            }

            private String prepareSkinPackage(SkinTheme skinTheme) {
                if (skinTheme.path.startsWith("file:///android_asset/")) { // copy to cache dir
                    return copyAssetSkinPackage(skinTheme.path, isNeedCopy(skinTheme)).getAbsolutePath();
                } else {
                    String path = Uri.parse(skinTheme.path).getPath();
                    if (!path.startsWith(SkinConfig.getSkinCacheDir(context).getAbsolutePath())) {
                        File file = copySkinPackage(path, isNeedCopy(skinTheme));
                        return file != null ? file.getAbsolutePath() : null;
                    } else {
                        return path;
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (callback != null) {
                    callback.onStart();
                }
            }

            @Override
            protected Resources doInBackground(SkinTheme... params) {
                try {
                    if (params.length == 1) {
                        SkinTheme skinTheme = params[0];
                        if (SkinUtil.isNewsTheme(skinTheme, SkinManager.this.currentTheme)) {
                            synchronized (lock) {
                                if (SkinUtil.isNewsTheme(skinTheme, SkinManager.this.currentTheme)) {
                                    if (TextUtils.isEmpty(skinTheme.path)) {
                                        return null;
                                    }
                                    String skinPkgPath = prepareSkinPackage(skinTheme);
                                    if (TextUtils.isEmpty(skinPkgPath)) {
                                        return null;
                                    }
                                    File file = new File(skinPkgPath);
                                    if (!file.exists()) {
                                        return null;
                                    }
                                    LogUtil.i("load skin package: " + skinPkgPath);

                                    PackageManager mPm = context.getPackageManager();
                                    PackageInfo mInfo = mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
                                    skinPackageName = mInfo.packageName;

                                    AssetManager assetManager = AssetManager.class.newInstance();
                                    Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                                    addAssetPath.invoke(assetManager, skinPkgPath);

                                    Resources superRes = context.getResources();
                                    //获取了最关键的Resource对象
                                    Resources skinResource = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());

                                    SkinUtil.saveCurrentTheme(context, skinTheme.name);
                                    SkinManager.this.currentTheme = skinTheme;
                                    isSystemSkin = false;
                                    return skinResource;
                                } else {
                                    return mResources;
                                }
                            }
                        } else {
                            return mResources;
                        }
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Resources result) {
                boolean hasSkinThemeChanged = mResources != result;
                mResources = result;
                if (mResources != null) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                    if (hasSkinThemeChanged) {
                        notifySkinUpdate();
                    }
                } else {
                    isSystemSkin = true;
                    if (callback != null) {
                        callback.onFailed();
                    }
                }
            }

        }.execute(theme);
        
#### 动态创建的View如何更新皮肤

需要调用dynamicAddView()方法，本质上就是把View加入一个需要更新主题的View的集合里。

	private void dynamicAddTitleView() {
		TextView textView = new TextView(getActivity());
		textView.setText("Small Article (动态new的View)");
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		textView.setLayoutParams(param);
		textView.setTextColor(getActivity().getResources().getColor(R.color.color_title_bar_text));
		textView.setTextSize(20);
		titleBarLayout.addView(textView);
		
		List<DynamicAttr> mDynamicAttr = new ArrayList<DynamicAttr>();
		mDynamicAttr.add(new DynamicAttr(AttrFactory.TEXT_COLOR, R.color.color_title_bar_text));
		dynamicAddView(textView, mDynamicAttr);
	}
	
核心代码：
![](https://raw.githubusercontent.com/JavaNoober/Skin/master/images/main3.png)

#### 使用以及skin包的制作

	xmlns:skin="http://schemas.android.com/android/skin"
	...
	  <TextView
	     ...
	     skin:enable="true" 
	     ... />
	     
	
skin包只需要存放相同文件名的资源文件即可，编译生成apk，将文件名改为.skin，以防用户误点。