package com.gigaspaces.mq.spacelistener;

import com.j_spaces.core.client.SQLQuery;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denys_Novikov
 * Date: 3/19/19
 */
public interface ReplicationHandler<T> {

    T createNewInstance(List parameters) throws ParseException;

    SQLQuery<T> createDeleteQuery(List parameters);

    UpdatePair getUpdate(List receivedList) throws ParseException;

    ArrayList spliter(String toSplit);
}
