package com.baseline.controller;

public class ActionFactory {
    private static ActionFactory instance = new ActionFactory();

    private ActionFactory() {
        super();
    }

    public static ActionFactory getInstance() {
        return instance;
    }

    public Action getAction(String command) {
        Action action = null;
        System.out.println("ActionFactory : " + command);

        if (command.equals("login")) {
            action = new LoginAction();
        } else if (command.equals("login_form")) {
            action = new LoginFormAction();
        } else if (command.equals("logout")) {
            action = new LogoutAction();
        } else if (command.equals("signup_form")) {
            action = new SignupFormAction();
        } else if (command.equals("signup")) {
            action = new SignupAction();
        }
        return action;
    }
}