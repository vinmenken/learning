#### 1、策略模式：可以实现目标的方案集合 为 一个策略
- 1、创建一个接口，抽象出方法；
- 2、实现该接口
- 3、通过上下文类（context）来使用某一种策略

具体 [代码](https://www.runoob.com/design-pattern/strategy-pattern.html)
```
public class Context {
   private Strategy strategy; //策略接口
 
   public Context(Strategy strategy){
      this.strategy = strategy;
   }
 
   public int executeStrategy(int num1, int num2){
      return strategy.doOperation(num1, num2); //每一个策略都会实现的方法
   }
}
```
```
public class StrategyPatternDemo {
   public static void main(String[] args) {
      Context context = new Context(new OperationAdd()); //创建上下文对象关指定使用哪一种策略    OperationAdd、OperationSubstract、OperationMultiply 不同的策略实现类
      System.out.println("10 + 5 = " + context.executeStrategy(10, 5));
 
      context = new Context(new OperationSubstract());      
      System.out.println("10 - 5 = " + context.executeStrategy(10, 5));
 
      context = new Context(new OperationMultiply());    
      System.out.println("10 * 5 = " + context.executeStrategy(10, 5));
   }
}
```
