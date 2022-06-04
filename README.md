# **RequestViewModel**
**长期更新 支持网络请求的ViewMode框架【ViewModel+LiveData+Retrofit】** 

github 地址：https://github.com/miaotaoii/RequestViewModel.git

### 优势：

快捷、方便地使用ViewModel 、LiveData管理数据，自动使用retrofit进行网络请求


无需关心LiveData和retroift request的创建，只要关注UI 控件更新数据的Bean对象

 RequestViewMode自动对LiveData进行缓存管理，每个retrofit api接口复用一个livedata

# 使用：
## 1.retrofit接口的声明
`RequestViewModel`内部使用`retrofit`进行网络请求，根据请求参数和返回的泛型类型管理api请求的创建，这一步是Retrofit的基本步骤；
你需要在初始化app时，额外使用`RetrofitUtil`配置Retrofit baseUrl

```java
RetrofitUtil.baseUrl = RetrofitDataApi.baseUrl;

```

```java
public interface RetrofitDataApi {

    public static final String requestOilprice = "/oilprice/index?key=3c5ee42145c852de4147264f25b858dc";
    public static final String baseUrl = "http://api.tianapi.com";
    
    @GET(requestOilprice)
    Call<ResponseJsonBean> getOliPrice(@Query("prov") String prov);
}
```



## 2.在Activity或Fragment中创建请求对象 
你需要设置请求参数，并在`RequestObj`构造器中传入retrofit api接口中的的GET或POST注解字符串。参数顺序必须保持和`requestObj` 的api注解对应的api接口参数一致


`RequestObj<T,V>` 第一个泛型声明api请求返回的类型，`T`类型支持本身为泛型类型；第二个泛型声明了RequestLiveData持有的数据类型，即UI更新所需要的数据类型；你将会在你自己继承`RequestLiveData`的类中，对返回数据进行转化解析并post到UI中

```java
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		... ...
		
//构建请求对象，设置请求api注解和参数,设置api返回对象类型和livedata数据类型
RequestObj<ResponseJsonBean, PriceBean> requestObj = new RequestObj<ResponseJsonBean, PriceBean>(RetrofitDataApi.requestOilprice) {
            @Override
            public Object[] getArgs() {
                return new Object[]{formatInputArg()};
            }
        };
		... ... 
}
```

## 3.继承RequestLiveData，处理返回数据
在这里将服务器返回的数据类型转换为UI需要的类型，并通过`LiveData post()`数据到UI

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

## 4.使用`RequestViewModel`和`RequestLiveData`请求数据

`RequestViewModel`由`RequestViewModelProvider`提供，你需要传入Retrofit api接口类型；你也可以自定义ViewModel继承自`RequestViewModel`来处理更多业务逻辑；每个`RequestViewModel`可以自动管理多个`RequestLiveData`，`RequestObj`中的retrofit api注解字符串决定了VeiwModel是否创建新的`RequestLiveData`或者复用旧的。



> **`RequestLiveData`将在首次创建时发出一次请求；如你正在使用google
> DataBinding框架，在`RequestLiveData` 接收数据并postValue后，数据将自动更新到UI控件。**



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
	RequestObj<ResponseJsonBean, PriceBean> requestObj = new RequestObj<ResponseJsonBean, 	PriceBean>(RetrofitDataApi.requestOilprice) {
            @Override
            public Object[] getArgs() {
                return new Object[]{formatInputArg()};
            }
        };

	liveData = requestViewModel.getRequestLiveData(requestObj, OliPriceLiveData.class);

	... ... 
}
```

## 5.设置请求参数，主动请求数据


你也可以使用`RequestLiveData` 的refresh 方法主动刷新数据；并使用`RequestObj` `setArgs()`方法设置新的参数。

```java
  requestObj.setArgs(new Object[]{"arg1",1,...});
  liveData.refresh();
```



## 6.观察RequestLvieData数据变化
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