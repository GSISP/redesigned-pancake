package com.acadgild.vpledge.constants;

/**
 * Created by pushp_000 on 5/22/2016.
 */
public class ServiceConstants {

    static final public String BASE_URL="http://vplez.com/";
    static final public String QUESTION="?";
    static final public String EQUAL="=";
    static final public String AND="&";
    static final public String SLASH="/";

    static final public String GET_PLEDGES="get_pledges";
    static final public String ADD_USER="add_user";
    static final public String ADD_PLEDGE_TO_USER="add_pledge_to_user";
    static final public String GET_USER_PLEDGES="get_user_pledges";
    static final public String GET_PLEDGE_PROGRESS="get_pledge_progress";
    static final public String UPDATE_PLEDGE="update_pledge_progress";
    static final public String GET_USER="get_user";

    static final public String REQUEST_POST="POST";
    static final public String REQUEST_GET="GET";

    static final public String ALL_PLEDGES_URL=BASE_URL+GET_PLEDGES;
    static final public String ADD_USER_URL=BASE_URL+ADD_USER;
    static final public String ADD_PLEDGE_TO_USER_URL=BASE_URL+ADD_PLEDGE_TO_USER;
    static final public String GET_USER_PLEDGE_URL=BASE_URL+GET_USER_PLEDGES;
    static final public String GET_PLEDGE_PROGRESS_URL=BASE_URL+GET_PLEDGE_PROGRESS;
    static final public String UPDATE_PLEDGE_URL=BASE_URL+UPDATE_PLEDGE;

    static final public String GET_USER_DETAILS_URL=BASE_URL+GET_USER;
}
