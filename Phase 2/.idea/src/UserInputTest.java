/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daler
 */
public class UserInputTest {
    public static void main(String args[]){
        int userID = UserInput.getUserID();
        System.out.println("UserInputTest > userID=" + userID);
        String msg = UserInput.getMessage();
        System.out.println(msg);
    }
}
