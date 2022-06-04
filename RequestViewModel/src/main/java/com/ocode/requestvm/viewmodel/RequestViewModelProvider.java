package com.ocode.requestvm.viewmodel;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

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
        T viewModel = new ViewModelProvider(owner).get(viewmodelcls);
        viewModel.setDataApi(dataApi);
        return viewModel;
    }
}