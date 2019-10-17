#### Bean的生命周期
1.Bean实例化。通过带参数的构造函数或者缺省构造函数创建Bean实例  
2.Bean初始化。赋值+调用用户定义的初始化方法   
3.Bean运行   
4.Bean销毁。DisposableBean.destroy()调用自定义的销毁方法