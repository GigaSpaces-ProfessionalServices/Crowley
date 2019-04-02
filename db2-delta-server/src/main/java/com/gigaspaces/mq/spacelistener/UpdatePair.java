package com.gigaspaces.mq.spacelistener;

import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.query.ISpaceQuery;

/**
 * @author Denys_Novikov
 * Date: 3/20/19
 */
public class UpdatePair {

    private ISpaceQuery key;
    private ChangeSet value;

    public UpdatePair(ISpaceQuery key, ChangeSet value) {
        this.key = key;
        this.value = value;
    }

    public ISpaceQuery getKey() {
        return key;
    }

    public ChangeSet getValue() {
        return value;
    }
}
