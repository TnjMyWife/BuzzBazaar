/* 判空 */
function isNull(obj) {
    if (obj == null || obj == undefined || obj.trim() == "") {
        return true;
    }
    return false;
}

/* 参数长度验证 */
function validLength(obj, length) {
    if (obj.trim().length < length) {
        return true;
    }
    return false;
}

/* 用户名称验证 4到16位（字母，数字，下划线，减号） */
function validUserName(userName) {
    var pattern = /^[\u4E00-\u9FA5\uF900-\uFA2D|\w]{2,8}$/;
    if (pattern.test(userName.trim())) {
        return true;
    } else {
        return false;
    }
}

/* 邮箱号正则验证 */
function validEmail(email) {
    var strRegex = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if(strRegex.test(email)){
        return true;
    }
    return false;
}

/* 用户密码验证 */
function validPassword(password) {
    // 包含至少一个数字，一个大写字母，一个小写字母，一个特殊符号，并且长度在6到20位之间
    var pattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{6,20}$/;
    if (pattern.test(password.trim())) {
        return true;
    } else {
        return false;
    }
}
