package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class JobOrderParent {
    private List<PandsToJobOrder> pandsToJobOrderList = new ArrayList<>();
    private int flag;
    private String message;
}
