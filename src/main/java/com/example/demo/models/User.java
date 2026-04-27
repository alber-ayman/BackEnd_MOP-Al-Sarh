package com.example.demo.models;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 50)
    @Column(name = "EMAIL")
    private String email;
    @Size(max = 120)
    @Column(name = "PASSWORD")
    private String password;
    @Size(max = 20)
    @Column(name = "USERNAME")
    private String username;

    private String role;
    @Column(name = "USERROLE")
    private BigInteger userrole;
    @Column(name = "ISACTIVE")
    private BigInteger isactive;
    @Column(name = "MERCHANT_ID")
    private BigInteger merchantId;
    @Column(name = "IS_FIRST_TIME")
    private BigInteger isFirstTime;

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
    private boolean allAuth;

    private boolean editUser;

    private boolean returnJobOrder;

    private boolean projectManager;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String username, String email, String password, String role,
                boolean viewProject, boolean editProject, boolean addProject, boolean deleteProject
            , boolean viewPand, boolean addPand, boolean editPand, boolean deletePand
            , boolean viewJobOrder, boolean addJobOrder, boolean editJobOrder, boolean deleteJobOrder
            , boolean viewExitJobOrder, boolean addExitJobOrder, boolean editExitJobOrder, boolean deleteExitJobOrder,
                boolean showReports, boolean recordDelivered, boolean allAuth,boolean viewOnly ,boolean editUser,boolean returnJobOrder, boolean projectManager) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;

        this.viewProject = viewProject;
        this.addProject = addProject;
        this.editProject = editProject;
        this.deleteProject = deleteProject;

        this.viewPand = viewPand;
        this.addPand = addPand;
        this.editPand = editPand;
        this.deletePand = deletePand;

        this.viewJobOrder = viewJobOrder;
        this.addJobOrder = addJobOrder;
        this.editJobOrder = editJobOrder;
        this.deleteJobOrder = deleteJobOrder;

        this.viewExitJobOrder = viewExitJobOrder;
        this.addExitJobOrder = addExitJobOrder;
        this.editExitJobOrder = editExitJobOrder;
        this.deleteExitJobOrder = deleteExitJobOrder;

        this.showReports = showReports;
        this.recordDelivered = recordDelivered;
        this.allAuth = allAuth;
        this.viewOnly = viewOnly;
        this.editUser = editUser;

        this.returnJobOrder = returnJobOrder;

        this.projectManager = projectManager;

    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
