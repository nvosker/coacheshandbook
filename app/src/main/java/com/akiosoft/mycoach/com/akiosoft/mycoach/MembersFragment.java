package com.akiosoft.mycoach.com.akiosoft.mycoach;

import com.akiosoft.coachapi.server.dao.coachApi.model.Member;

/**
 * Created by nvoskeritchian on 9/12/14.
 */
public class MembersFragment extends AbstractModelFragment<Member> {
    @Override
    protected boolean onNewActionItemSelected() {
        return false;
    }

    @Override
    protected void onDeleteItemConfirmed(Member item) {

    }
}
