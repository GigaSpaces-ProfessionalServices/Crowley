package com.gigaspaces.mq.spacelistener;

import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.mq.common.Employee;
import com.gigaspaces.query.IdQuery;
import com.j_spaces.core.client.SQLQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denys_Novikov
 * Date: 3/19/19
 */
public class EmployeeHandler implements ReplicationHandler<Employee> {

    @Override
    public Employee createNewInstance(List parameters) {
        return new Employee(
                parameters.get(0).toString(),
                parameters.get(1).toString(),
                parameters.get(2).toString(),
                parameters.get(3).toString(),
                parameters.get(4).toString(),
                parameters.get(5).toString(),
                parameters.get(6).toString(),
                parameters.get(7).toString(),
                parameters.get(8).toString(),
                parameters.get(9).toString(),
                parameters.get(10).toString(),
                parameters.get(11).toString(),
                parameters.get(12).toString(),
                parameters.get(13).toString()
        );
    }

    @Override
    public SQLQuery<Employee> createDeleteQuery(List parameters) {
        String querystr = "ID = '" + parameters.get(12).toString() + "'";
        return new SQLQuery<>(Employee.class, querystr);
    }

    @Override
    public UpdatePair getUpdate(List receivedList) {
        IdQuery<Employee> idQuery = new IdQuery<>(Employee.class, String.valueOf(receivedList.get(0).toString()));
        return new UpdatePair(idQuery, createChangeSetForEmployee(receivedList));
    }

    @Override
    public ArrayList spliter(String toSplit) {

        ArrayList al = new ArrayList();

        String[] splitedStr = toSplit.split(",");

        int countstrt = (splitedStr.length - 12) / 2;
//        System.out.println("No. of columns:" + countstrt);
        int i = 1;
        for (int countup = splitedStr.length - countstrt; countup < splitedStr.length; countup++) {
//            System.out.println("["+i+"]"+splitedStr[countup]);
            al.add(splitedStr[countup]);
            i++;

        }

        return al;
    }

    private ChangeSet createChangeSetForEmployee(List receivedList) {
        return new ChangeSet().set("FIRSTNAME", receivedList.get(1).toString())
                .set("MIDINIT", receivedList.get(2).toString())
                .set("LASTNAME", receivedList.get(3).toString())
                .set("WORKDEPT", receivedList.get(4).toString())
                .set("PHONENO", receivedList.get(5).toString())
                .set("HIREDATE", receivedList.get(6).toString())
                .set("JOB", receivedList.get(7).toString())
                .set("EDLEVEL", receivedList.get(8).toString())
                .set("SEX", receivedList.get(9).toString())
                .set("BIRTHDAY", receivedList.get(10).toString())
                .set("SALARY", receivedList.get(11).toString())
                .set("BONUS", receivedList.get(12).toString())
                .set("COMM", receivedList.get(13).toString());
    }
}
