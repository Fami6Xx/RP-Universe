package me.fami6xx.rpuniverse.core.jobs;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;

import java.util.HashMap;
import java.util.Map;

public class Position {
    private String name;
    private double salary;
    private int workingStepPermissionLevel;
    private boolean isBoss;
    private boolean isDefault;

    /**
     * Position full-argument constructor
     *
     * @param name Name of the position
     * @param salary Salary of the position
     * @param workingStepPermissionLevel Working step permission level of the position
     * @param isBoss Boolean flag if position is a boss
     * @param isDefault Boolean flag if position is default
     */
    public Position(String name, int salary, int workingStepPermissionLevel, boolean isBoss, boolean isDefault) {
        this.name = name;
        this.salary = salary;
        this.workingStepPermissionLevel = workingStepPermissionLevel;
        this.isBoss = isBoss;
        this.isDefault = isDefault;
    }

    /**
     * Getter method for name
     * @return Name of the position
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for name
     * @param name New name of the position
     */
    public void setName(String name) {
        this.name = name;
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(job -> job.getPositions().contains(this), MenuTag.JOB_POSITION, MenuTag.JOB_POSITION_INTERNAL);
    }

    /**
     * Getter method for salary
     * @return Salary of the position
     */
    public double getSalary() {
        return salary;
    }

    /**
     * Setter method for salary
     * @param salary New salary of the position
     */
    public void setSalary(double salary) {
        this.salary = salary;
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(job -> job.getPositions().contains(this), MenuTag.JOB_POSITION, MenuTag.JOB_POSITION_INTERNAL);
    }

    /**
     * Getter method for working step permission level
     * @return Working step permission level of the position
     */
    public int getWorkingStepPermissionLevel() {
        return workingStepPermissionLevel;
    }

    /**
     * Setter method for working step permission level
     * @param workingStepPermissionLevel New working step permission level of the position
     */
    public void setWorkingStepPermissionLevel(int workingStepPermissionLevel) {
        this.workingStepPermissionLevel = workingStepPermissionLevel;
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(job -> job.getPositions().contains(this), MenuTag.JOB_POSITION, MenuTag.JOB_POSITION_INTERNAL);
    }

    /**
     * Getter method for isBoss
     * @return If the position is a boss position
     */
    public boolean isBoss() {
        return isBoss;
    }

    /**
     * Setter method for isBoss
     * @param boss If the position is a boss position
     */
    public void setBoss(boolean boss) {
        isBoss = boss;
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(job -> job.getPositions().contains(this), MenuTag.JOB_POSITION, MenuTag.JOB_POSITION_INTERNAL);
    }

    /**
     * Getter method for isDefault
     * @return If the position is a default position
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Setter method for isDefault
     * @param aDefault If the position is a default position
     */
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(job -> job.getPositions().contains(this), MenuTag.JOB_POSITION, MenuTag.JOB_POSITION_INTERNAL);
    }

    /**
     * Returns a string representation of the Position object.
     *
     * @return A string representation of the Position object, including the name, salary,
     *         working step permission level, boss flag, and default flag.
     */
    @Override
    public String toString() {
        return "Position{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                ", workingStepPermissionLevel=" + workingStepPermissionLevel +
                ", isBoss=" + isBoss +
                ", isDefault=" + isDefault +
                '}';
    }

    /**
     * Constructs a Position object from a string representation.
     *
     * @param s The string representation of the Position object.
     *          The string should be in the format "{key1=value1, key2=value2, ...}".
     *          The following keys are expected:
     *          - name: Name of the position (String)
     *          - salary: Salary of the position (int)
     *          - workingStepPermissionLevel: Working step permission level of the position (int)
     *          - isBoss: Boolean flag if position is a boss (boolean)
     *          - isDefault: Boolean flag if position is default (boolean)
     *
     * @return A Position object created from the given string representation,
     *         or null if any error occurred during the conversion.
     */
    public static Position fromString(String s) {
        try {
            s = s.substring(s.indexOf('{') + 1, s.lastIndexOf('}'));
            String[] keyValuePairs = s.split(", ");
            Map<String, String> map = new HashMap<>();

            for (String pair : keyValuePairs) {
                String[] entry = pair.split("=");
                map.put(entry[0].trim(), entry[1].trim().replaceAll("'", ""));
            }

            String name = map.get("name");
            int salary = Integer.parseInt(map.get("salary"));
            int workingStepPermissionLevel = Integer.parseInt(map.get("workingStepPermissionLevel"));
            boolean isBoss = Boolean.parseBoolean(map.get("isBoss"));
            boolean isDefault = Boolean.parseBoolean(map.get("isDefault"));

            return new Position(name, salary, workingStepPermissionLevel, isBoss, isDefault);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}