/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daler
 */
public class MessageTest {

    public static void main(String args[]) {
        Message.sentMessageToUser("1");
        Message.sendMessageToGroup("83");
        Message.displayMessages("83");
        Message.displayNewMessages("1");
        Message.topMessages();
    }
}
