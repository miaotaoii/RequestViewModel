package com.ocode.requestvm.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.ocode.requestvm.util.Utils;

/**
 * @author:eric
 * @date:6/2/22
 */
public class RequestViewModelProvider {
    private static final RequestViewModelProvider ourInstance = new RequestViewModelProvider();

    public static RequestViewModelProvider getInstance() {
        return ourInstance;
    }

    private RequestViewModelProvider() {
    }

    public <A, T extends RequestViewModel> T get(ViewModelStoreOwner owner, Class<A> dataApi, Class<T> viewmodelcls) {
        Utils.checkNotNull(viewmodelcls, "viewmodelcls can' be null");
        Utils.checkNotNull(dataApi, "retrofit api interface can' be null");
        T viewModel = new ViewModelProvider(owner).get(viewmodelcls);
        if (owner instanceof LifecycleOwner) {
            ((LifecycleOwner) owner).getLifecycle().addObserver(viewModel);
        }
        viewModel.setDataApi(dataApi);
        return viewModel;
    }
}