package com.ericliudeveloper.mvpevent.model;

/**
 * Created by liu on 23/05/15.
 */
public abstract class DaoFactory {
    public enum DaoFactoryType{
        CONTENT_PROVIDER;
    }


    public abstract FirstModelDAO getFirstModelDAO();

    protected DaoFactory(){}

    public static  DaoFactory getDaoFactory(DaoFactoryType typeOfFactory) {
        switch (typeOfFactory) {
            case CONTENT_PROVIDER:
                return new ProviderDaoFactory();

            default: throw new UnsupportedOperationException("DaoFactory Type not found.");
        }
    }
}
