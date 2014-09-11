package com.akiosoft.mycoach;

import com.akiosoft.coachapi.server.dao.coachApi.model.Sport;
import com.google.api.client.util.Lists;

import java.util.ArrayList;


/**
 * Created by nvoskeritchian on 9/7/14.
 */
public class Application extends android.app.Application {
    public ArrayList<Sport> greetings = Lists.newArrayList();
}
