package cn.zybwz.binmedia.bean

class FilterInfo {
    var showName:String=""
    var name:String=""
    var realTimeFormat=""
    var cmdFormat=""
    var defaultArg = arrayOf("")

    constructor(showName: String, name: String, realTimeFormat: String, cmdFormat: String) {
        this.showName = showName
        this.name = name
        this.realTimeFormat = realTimeFormat
        this.cmdFormat = cmdFormat
    }

    constructor(
        showName: String,
        name: String,
        realTimeFormat: String,
        cmdFormat: String,
        defaultArg: Array<String>
    ) {
        this.showName = showName
        this.name = name
        this.realTimeFormat = realTimeFormat
        this.cmdFormat = cmdFormat
        this.defaultArg = defaultArg
    }


}