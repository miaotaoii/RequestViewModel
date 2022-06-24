# **RequestViewModel**
**长期更新 支持网络请求的ViewMode框架【ViewModel+LiveData+Retrofit】** 

github 地址：https://github.com/miaotaoii/RequestViewModel.git

### 优势：

快捷、方便地使用ViewModel 、LiveData管理数据，自动使用retrofit进行网络请求


无需关心LiveData和retroift request的创建，只要关注UI 控件更新数据的Bean对象

RequestViewMode自动对LiveData进行缓存管理，每个retrofit api接口复用一个livedata

# Gradle 
项目根目录下 build.gradle 添加

```
allprojects {
    repositories {
        google()
        maven { url 'https://jitpack.io' }
        jcenter()
    }
}
```
module的build.gradle 中添加：

```
dependencies {

	implementation 'com.github.miaotaoii:RequestViewModel:1.0.7'

}
```
# 使用

## 1.retrofit接口的声明
`RequestViewModel`内部使用`retrofit`进行网络请求，框架会根据请求的注解字和参数及返回值类型管理retrofit请求对象的创建；第一步是Retrofit的基本步骤；


```java
public interface RetrofitDataApi {

    public static final String requestOilprice = "/oilprice/index?key=3c5ee42145c852de4147264f25b858dc";
    public static final String baseUrl = "http://api.tianapi.com";
    
    //ResponseJsonBean对象是自定义的服务器返回json类型，可以是泛型类型，如 ResponseData<UserInfo>
    @GET(requestOilprice)
    Call<ResponseJsonBean> getOliPrice(@Query("prov") String prov);
}
```
## 2.retrofit配置
你需要在初始化app时，额外使用`RetrofitConfig`配置你自己的Retrofit实例或使用默认创建retrofit实例
### 方式1：
使用项目已有的retrofit实例
```  
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitDataApi.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .build())
                .build();
RetrofitConfig.getInstance(retrofit).init();

```
### 方式2：
设置baseurl，框架会帮你创建默认的retrofit实例
```
RetrofitConfig.getInstance(RetrofitDataApi.baseUrl).init();

```

## 3.在Activity或Fragment中创建请求对象 
你需要设置请求参数，并在`RequestObj`构造器中传入retrofit api接口中的的GET或POST注解字符串。参数顺序必须保持和`requestObj` 的api注解对应的api接口参数一致


`RequestObj<T>`  泛型声明api请求返回的类型，`T`类型支持本身为泛型类型； 你将会在你自己继承`RequestLiveData`的类中，对返回数据进行转化解析并post到UI中

```java
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		... ...
		
//构建请求对象，设置请求api注解和参数,设置api返回对象类型和livedata数据类型
RequestObj<ResponseJsonBean> requestObj = new RequestObj<ResponseJsonBean>(RetrofitDataApi.requestOilprice) {
            @Override
            public Object[] getArgs() {
                return new Object[]{formatInputArg()};
            }
        };
		... ... 
}
```

## 4.继承RequestLiveData，处理返回数据
在这里将服务器返回的数据类型转换为UI需要的类型，并通过`LiveData post()`数据到UI。第一个泛型参数是retrofit请求返回的数据类型，第二个泛型参数是LiveData持有的数据类型。

```java
public class OliPriceLiveData extends RequestLiveData<ResponseJsonBean, PriceBean> {
    @Override
    public void onLoadSuccess(ResponseJsonBean data) {
        if (data.getCode() == 200) {
            PriceBean priceBean = data.getNewslist().get(0);
            priceBean.setCode(200);
            postValue(priceBean);
        } else {
            PriceBean priceBean = new PriceBean();
            priceBean.setCode(data.getCode());
            priceBean.setMsg(data.getMsg());
            postValue(priceBean);
        }
    }

    @Override
    public void onLoadFailed(int code, String msg) {
        PriceBean priceBean = new PriceBean();
        priceBean.setCode(code);
        priceBean.setMsg(msg);
        postValue(priceBean);
    }
}
```

## 5.使用`RequestViewModel`和`RequestLiveData`请求数据

`RequestViewModel`由`RequestViewModelProvider`提供，你需要传入Retrofit api接口类型；你也可以自定义ViewModel继承自`RequestViewModel`来处理更多业务逻辑；每个`RequestViewModel`可以自动管理多个`RequestLiveData`，`RequestObj`中的retrofit api注解字符串决定了VeiwModel是否创建新的`RequestLiveData`或者复用旧的。



> **`RequestLiveData`将根据传入的第三个参数决定是否在首次创建时发出一次请求；如你正在使用google
> DataBinding框架，建议传入true，在`RequestLiveData` 接收数据并postValue后，数据将自动更新到UI控件。**



```java
private RequestViewModel requestViewModel;
private OliPriceLiveData liveData;

protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	... ...
	requestViewModel = RequestViewModelProvider.getInstance().get(
						    this,
							RetrofitDataApi.class,
	 					    RequestViewModel.class
	 					    ); 
//构建请求对象，设置请求api注解和参数,设置api返回对象类型和livedata数据类型
	RequestObj<ResponseJsonBean> requestObj = new RequestObj<ResponseJsonBean>(RetrofitDataApi.requestOilprice) {
            @Override
            public Object[] getArgs() {
                return new Object[]{formatInputArg()};
            }
        };

	liveData = requestViewModel.getRequestLiveData(requestObj, OliPriceLiveData.class,true);

	... ... 
}
```

## 6.设置请求参数，主动请求数据


你也可以使用`RequestLiveData` 的refresh 方法主动刷新数据；并使用`RequestObj` `setArgs()`方法设置新的参数。

```java
  requestObj.setArgs(new Object[]{"arg1",1,...});
  liveData.refresh();
```



## 7.观察RequestLvieData数据变化
同样作为`LiveData`的子类，你也可以使用observe接口观察数`RequestLiveData`据变化


```java
 liveData.observe(this, new Observer<PriceBean>() {
            @Override
            public void onChanged(PriceBean priceBean) {
                if (priceBean.getCode() != 200) {
                    Toast.makeText(MainActivity.this, "请求失败 code =" + priceBean.getCode() + " msg = " + priceBean.getMsg()
                            , Toast.LENGTH_SHORT).show();
                } else {
                    //更新ui ，此处使用dataBinding 自动更新到ui
                    Log.i("MainActivity", "price bean onchanged " + priceBean.toString());
                }
            }
        });
```

## 8.日志打印
默认只打印ERROR日志，INFO日志开启后将打印所有请求执行的api接口方法签名、请求参数、请求response code以及处理请求的对象hash值。

```java
RetrofitConfig.setLogLevel(Logger.LogLevel.INFO);
```

```java

I/[RequestViewModel]: TypedRequest[com.ocode.requestvm.request.TypedRequestImpl@96f475c] ------>[interface com.requestVM.demo.api.RetrofitDataApi]  (public abstract retrofit2.Call<com.requestVM.demo.api.ResponseJsonBean> com.requestVM.demo.api.RetrofitDataApi.getOliPrice(java.lang.String,java.lang.String)) args{上海,test,}
I/[RequestViewModel]: TypedRequest[com.ocode.requestvm.request.TypedRequestImpl@96f475c ]onResponse call return success code=200

```

## 9.更多用法
### 9.1如何实现一个api接口对应多个不同的livedata对象 【支持版本1.0.5及以上】
在1.0.5之前的版本中，通过 ``` new RequestObj<ResponseJsonBean>(RetrofitDataApi.requestOilprice) ```构建请求对象时，传入的api 注解字符串将作为 RequestLiveData 默认的key，因此同一个RequestViewModel中的Map容器中，对于同一个retrofit api，只会缓存唯一一个LiveData；

现在，你可以使用   ``` public RequestObj(String apiAnnotation, String requestKey) {...}``` 接口来创建自定义requestKey的```RequestObj```,这可以让你在同一个RequestViewModel中缓存使用同一个api接口的不同的LiveData对象，比如多个Fragment使用同一个api接口，但是要同时使用不同的LiveData来持有对应的页面数据；

### 9.2关于及时释放内存的处理
1.框架内部处理了Activity重建的时的情况，保证LiveData对象一直存在于ViewModel声明周期中，且在Activity真正finished时不发生内存泄漏的同时，在Activity由于旋转或处于后台销毁重建时，不影响已有的请求事件，且不影响后续的事件请求。

2.处理了ViewModel生命周期,在ViewModel生命周期结束时，对request请求对象内存进行清理，避免内存泄漏。自动取消所有未完成的网络请求，清理回调，节省资源。

3.支持使用不同的request obj对象请求同一api的行为；可以在每次getLiveData时传入新的RequestObj对象，只要request key（request key默认是retrofit api的注解字符串，也可以使用自定义的key值） 是不变的，就会复用旧的LiveData，并且会刷新LiveData内部持有的RequestObj对象为最新设置的对象。









