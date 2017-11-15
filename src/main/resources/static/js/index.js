/**
 * Created by Administrator on 2017/11/14.
 */
/*构造函数法*/
function dog() {
    this.name="狗";
}
var dog=new dog();
alert(dog.name)
dog.prototype.makeSound=function () {
    alert("汪汪汪");
}

/*Object.create*/
//noinspection JSAnnotator
var Cat={
    name="猫名字",
    makeSound:function () {
        alert("喵喵喵")
    }
}
var cat=Object.create(Cat);
cat.makeSound();

