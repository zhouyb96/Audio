package cn.zybwz.binmedia.bean;

import cn.zybwz.binmedia.UtilKt;

public class FilterInfo {
    private String showName;
    private String name;
    private String liveCmd;//
    private String fileCmd;
    private String[] args;
    private String fileIn;
    private String fileOut;

    public FilterInfo(String showName, String name, String liveCmd, String fileCmd, String[] args) {
        this.showName = showName;
        this.name = name;
        this.liveCmd = liveCmd;
        this.fileCmd = fileCmd;
        this.args = args;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLiveCmd() {
        return liveCmd;
    }

    public void setLiveCmd(String liveCmd) {
        this.liveCmd = liveCmd;
    }

    public String getFileCmd() {
        return fileCmd;
    }

    public void setFileCmd(String fileCmd) {
        this.fileCmd = fileCmd;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getFileIn() {
        return fileIn;
    }

    public void setFileIn(String fileIn) {
        this.fileIn = fileIn;
    }

    public String getFileOut() {
        return fileOut;
    }

    public void setFileOut(String fileOut) {
        this.fileOut = fileOut;
    }

    public String buildLiveCmd(){
        return String.format(liveCmd, (Object[])args);
    }

    public String buildFileCmd(){
        String[] strings=new String[args.length+2];
        System.arraycopy(args,0,strings,1,args.length);
        strings[0]=fileIn;
        strings[strings.length-1]=fileOut;
        return String.format(fileCmd, (Object[])strings);
    }
}
