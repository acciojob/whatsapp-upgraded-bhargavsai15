package com.driver.Repository;


import com.driver.Group;
import com.driver.Message;
import com.driver.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {


    HashMap<Group,List<User>> groupUserMap;
    HashMap<Group,User> groupAdmin;
    HashMap<Group, List<Message>> groupMessageMap;
    HashMap<Message,User> messageUserHashMap;
    HashSet<String> user;
    int groupCount=0;
    int messageCount=0;

    public WhatsappRepository(){
        this.user=new HashSet<>();
        this.groupAdmin=new HashMap<>();
        this.groupUserMap=new HashMap<>();
        this.groupMessageMap=new HashMap<>();
        this.messageUserHashMap=new HashMap<>();
    }


    public String addUser(String name,String mobile) throws Exception {
        if(user.contains(mobile)){
            throw new Exception("User already exists");
        }

        user.add(mobile);
        User user1=new User(name,mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        if(user.size()==2){
            Group group=new Group(users.get(1).getName(),2);
            groupAdmin.put(group,users.get(0));
            groupUserMap.put(group,users);
            groupMessageMap.put(group,new ArrayList<>());
            return group;
        }
        this.groupCount+=1;
        Group group=new Group(new String("Group "+this.groupCount),users.size());
        groupAdmin.put(group,users.get(0));
        groupUserMap.put(group,users);
        groupMessageMap.put(group,new ArrayList<>());
        return group;
    }

    public int createMessage(String message){
        this.messageCount+=1;
        Message message1=new Message(messageCount,message);
        return message1.getId();
    }

    public int sendMessage(Message message,User sender,Group group) throws Exception {
        if(groupAdmin.containsKey(group)){
            List<User> users = groupUserMap.get(group);
            Boolean userFound = false;
            for(User user: users){
                if(user.equals(sender)){
                    userFound = true;
                    break;
                }
            }
            if(userFound){
                messageUserHashMap.put(message, sender);
                List<Message> messages = groupMessageMap.get(group);
                messages.add(message);
                groupMessageMap.put(group, messages);
                return messages.size();
            }
            throw new Exception("You are not allowed to send message");
        }
        throw new Exception("Group does not exist");
    }


    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(groupAdmin.containsKey(group)){
            if(groupAdmin.get(group).equals(approver)){
                List<User> participants = groupUserMap.get(group);
                Boolean userFound = false;
                for(User participant: participants){
                    if(participant.equals(user)){
                        userFound = true;
                        break;
                    }
                }
                if(userFound){
                    groupAdmin.put(group, user);
                    return "SUCCESS";
                }
                throw new Exception("User is not a participant");
            }
            throw new Exception("Approver does not have rights");
        }
        throw new Exception("Group does not exist");
    }


    public int removeuser(User user) {
        return 1;
    }

    public String findMessage(Date start, Date end, int K) throws Exception {
        List<Message> messages = new ArrayList<>();
        for(Group group: groupMessageMap.keySet()){
            messages.addAll(groupMessageMap.get(group));
        }
        List<Message> filteredMessages = new ArrayList<>();
        for(Message message: messages){
            if(message.getTimestamp().after(start) && message.getTimestamp().before(end)){
                filteredMessages.add(message);
            }
        }
        if(filteredMessages.size() < K){
            throw new Exception("K is greater than the number of messages");
        }
        Collections.sort(filteredMessages, new Comparator<Message>(){
            public int compare(Message m1, Message m2){
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            }
        });
        return filteredMessages.get(K-1).getContent();
    }
}
