package com.lzf.stackwatcher.entity.judge;

import java.util.Objects;

public class WarnMachineAssociate {

    private Integer policyId;
    private String machineUUID;

    public WarnMachineAssociate() { }

    public WarnMachineAssociate(int policyId, String machineUUID) {
        Objects.requireNonNull(machineUUID);
        this.policyId = policyId;
        this.machineUUID = machineUUID;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getMachineUUID() {
        return machineUUID;
    }

    public void setMachineUUID(String machineUUID) {
        this.machineUUID = machineUUID;
    }
}
