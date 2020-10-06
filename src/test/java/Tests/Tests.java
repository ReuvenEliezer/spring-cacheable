package Tests;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tests {


    @Test
    public void StopWatch() throws InterruptedException {
        StopWatch stopWatch = new StopWatch("My Stop Watch");

        stopWatch.start("initializing");
        Thread.sleep(2000); // simulated work
        stopWatch.stop();

        stopWatch.start("processing");
        Thread.sleep(5000); // simulated work
        stopWatch.stop();

        stopWatch.start("finalizing");
        Thread.sleep(3000); // simulated work
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }

    @Test
    public void switchTest() {
        RuleActionType ruleActionType = RuleActionType.IrrigationStart;

        for (int i = 0; i < 2; i++) {
            switch (ruleActionType) {
                case IrrigationStart:
                    System.out.println("result = " + ruleActionType);
                    break;
            }
        }
    }

    public enum RuleActionType {

        IrrigationStart,
        IrrigationPause,
        IrrigationResume,
        IrrigationStop

    }

    @Test
    public void stream1() {
        ConditionValue conditionValue1 = new ConditionValue(2, 2d);
        ConditionValue conditionValue2 = new ConditionValue(2, 3d);
        ConditionValue conditionValue3 = new ConditionValue(2, 2d);
        HashSet<ConditionValue> conditionValues = new HashSet();
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);
        conditionValues.add(conditionValue3);

        Iterator<ConditionValue> iterator = conditionValues.iterator();
        while (iterator.hasNext()) {
            ConditionValue conditionValue = iterator.next();
            Supplier<Stream<ConditionValue>> conditionValueStream =  () -> conditionValues.stream().filter(o1 -> o1.getDeviceId().equals(conditionValue.getDeviceId()));
            if (conditionValueStream.get().filter(o1 -> o1.getValue().equals(conditionValue.getValue())).count() >= 2) {
                System.out.println("valid result = size"+conditionValueStream.get().count());
            }
            System.out.println("valid result = size  "+conditionValueStream.get().count());
        }
    }

    @Test
    public void stream() {
        ConditionValue conditionValue1 = new ConditionValue(1, 2d);
        ConditionValue conditionValue2 = new ConditionValue(1, 2d);
        ConditionValue conditionValue3 = new ConditionValue(1, 2d);
        HashSet<ConditionValue> conditionValues = new HashSet();
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);
        conditionValues.add(conditionValue3);

        boolean valid = isValid(conditionValues);
        Assert.assertFalse(valid);
        System.out.println("valid result = " + valid);


        conditionValues.clear();
        conditionValue1 = new ConditionValue(1, 1d);
        conditionValue2 = new ConditionValue(1, 1d);
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);

        valid = isValid(conditionValues);
        Assert.assertFalse(valid);
        System.out.println("valid result = " + valid);

        conditionValues.clear();
        conditionValue1 = new ConditionValue(1, 1d);
        conditionValue2 = new ConditionValue(1, 2d);
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);

        valid = isValid(conditionValues);
        Assert.assertTrue(valid);
        System.out.println("valid result = " + valid);

        conditionValues.clear();
        conditionValue1 = new ConditionValue(1, 1d);
        conditionValue2 = new ConditionValue(2, 1d);
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);

        valid = isValid(conditionValues);
        Assert.assertTrue(valid);
        System.out.println("valid result = " + valid);


        conditionValues.clear();
        conditionValue1 = new ConditionValue(1, 1d);
        conditionValue2 = new ConditionValue(2, 2d);
        conditionValue3 = new ConditionValue(3, 3d);
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);
        conditionValues.add(conditionValue3);

        valid = isValid(conditionValues);
        Assert.assertTrue(valid);
        System.out.println("valid result = " + valid);


        conditionValues.clear();
        conditionValue1 = new ConditionValue(1, 1d);
        conditionValue2 = new ConditionValue(2, 1d);
        conditionValue3 = new ConditionValue(3, 1d);
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);
        conditionValues.add(conditionValue3);

        valid = isValid(conditionValues);
        Assert.assertTrue(valid);
        System.out.println("valid result = " + valid);

        conditionValues.clear();
        conditionValue1 = new ConditionValue(1, 2d);
        conditionValue2 = new ConditionValue(2, 2d);
        conditionValue3 = new ConditionValue(1, 1d);
        conditionValues.add(conditionValue1);
        conditionValues.add(conditionValue2);
        conditionValues.add(conditionValue3);

        valid = isValid(conditionValues);
        Assert.assertTrue(valid);
        System.out.println("valid result = " + valid);
    }

    private boolean isValid(HashSet<ConditionValue> conditionValues) {
        Iterator<ConditionValue> iterator = conditionValues.iterator();
        while (iterator.hasNext()) {
            ConditionValue conditionValue = iterator.next();
            if (conditionValues.stream().filter(o1 -> o1.getDeviceId().equals(conditionValue.getDeviceId())).filter(o1 -> o1.getValue().equals(conditionValue.getValue())).count() >= 2) {
                return false;
            }
        }

        return true;
    }

    class ConditionValue {
        private Integer deviceId;
        private Double value;

        public ConditionValue(Integer deviceId, Double value) {
            this.deviceId = deviceId;
            this.value = value;
        }

        public Double getValue() {
            return value;
        }

        public Integer getDeviceId() {
            return deviceId;
        }
    }
}
