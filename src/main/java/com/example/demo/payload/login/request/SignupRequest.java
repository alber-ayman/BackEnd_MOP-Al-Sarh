package com.example.demo.payload.login.request;

import lombok.Data;

@Data
public class SignupRequest {

    private String username;

    private String email;

    private String role;

//    private String oldPassword;

    private String password;

    private boolean viewProject;
    private boolean addProject;
    private boolean editProject;
    private boolean deleteProject;

    private boolean viewPand;
    private boolean addPand;
    private boolean editPand;

    private boolean deletePand;

    private boolean viewJobOrder;
    private boolean addJobOrder;
    private boolean editJobOrder;

    private boolean deleteJobOrder;

    private boolean viewExitJobOrder;
    private boolean addExitJobOrder;
    private boolean editExitJobOrder;

    private boolean deleteExitJobOrder;
    private boolean showReports;
    private boolean recordDelivered;

    private boolean viewOnly;
    private boolean editUser;
    private boolean allAuth;

    private boolean returnJobOrder;

    private boolean projectManager;
}
