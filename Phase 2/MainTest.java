/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daler
 */
public class MainTest {

    public static void main(String avg[]) {
        System.out.println("Calling sentMessageToUser for testing . ");
        Message.isTest = true;
        Message.sentMessageToUser("1");
        System.out.println("******************************************");

        System.out.println("Calling sendMessageToGroup for testing . ");
        Message.sendMessageToGroup("83");
        System.out.println("******************************************");

        System.out.println("Calling displayMessages for testing . ");
        Message.displayMessages("83");
        System.out.println("******************************************");

        System.out.println("Calling displayNewMessages for testing . ");
        Message.displayNewMessages("1");
        System.out.println("******************************************");

        System.out.println("Calling topMessages with k=10 x=10 for testing . ");
        Message.topMessages();
        System.out.println("******************************************");

        Group.isTest = true;
        System.out.println("Calling createGroup for testing . ");
        Group.createGroup("2");
        System.out.println("******************************************");

        System.out.println("Calling initiateAddingGroup for testing . ");
        Group.initiateAddingGroup("1");
        System.out.println("******************************************");

    }
}
