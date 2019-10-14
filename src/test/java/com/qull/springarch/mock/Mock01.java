//package com.qull.springarch.mock;
//
//import javafx.beans.binding.When;
//import org.junit.Test;
//import org.mockito.*;
//import org.mockito.internal.verification.VerificationModeFactory;
//import org.mockito.verification.VerificationMode;
//
//import java.security.PrivateKey;
//import java.sql.Time;
//import java.util.LinkedList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.Mockito.*;
//
///**
// * @author kzh
// * @description
// * @DATE 2019/10/14 7:34
// */
//public class Mock01 {
//
//    @Test
//    public void verify() {
//        List<String> mockList = mock(List.class);
//        mockList.add("one");
//        mockList.clear();
//
//        Mockito.verify(mockList).add("one");
//        Mockito.verify(mockList).clear();
//    }
//
//    @Test
//    public void stubbing() {
//        // 构建一个LinkedList实例
//        LinkedList mockedList = mock(LinkedList.class);
//
//        // 调用get(0)时返回first
//        when(mockedList.get(0)).thenReturn("first");
//        // 调用get(1)时抛出运行时异常
//        when(mockedList.get(1)).thenThrow(new RuntimeException());
//
//        // print first
//        System.out.println(mockedList.get(0));
//        // exception
////        System.out.println(mockedList.get(1));
//        // null
//        System.out.println(mockedList.get(99));
//
//        Mockito.verify(mockedList).get(0);
//    }
//
//    @Test
//    public void argument() {
//        List mock = mock(List.class);
//        // anyInt
//        when(mock.get(anyInt())).thenReturn("element");
//        //
//        when(mock.contains(argThat((ArgumentMatcher<String>) argument -> true))).thenReturn(true);
//
//        // element
//        System.out.println(mock.get(99));
//
//        Mockito.verify(mock).get(anyInt());
//        Mockito.verify(mock).add("hahaha");
//
//    }
//
//    @Test
//    public void invocationNums() {
//        List<String> mock = mock(List.class);
//        mock.add("once");
//
//        mock.add("twice");
//        mock.add("twice");
//
//        mock.add("three times");
//        mock.add("three times");
//        mock.add("three times");
//
//        Mockito.verify(mock).add("once");
//        Mockito.verify(mock, times(1)).add("once");
//
//        Mockito.verify(mock, times(2)).add("twice");
//        Mockito.verify(mock, times(3)).add("three times");
//        Mockito.verify(mock, never()).add("never happened");
//        // 因为没有调用过  mock.add("error count")所以这里的校验通不过
////        Mockito.verify(mock, times(1)).add("error count");
//
//        Mockito.verify(mock, VerificationModeFactory.atLeast(1)).add("once");
//        Mockito.verify(mock, atLeastOnce()).add("once");
//        Mockito.verify(mock, atLeast(2)).add("twice");
//        Mockito.verify(mock, atMost(3)).add("three times");
//    }
//
//
//    @Test
//    public void exception() {
//        List mock = mock(List.class);
//        doThrow(new RuntimeException()).when(mock).clear();
//        mock.clear();
//    }
//
//    @Test
//    public void order() {
//        List<String> singleMock = mock(List.class);
//        singleMock.add("was added first");
//        singleMock.add("was added second");
//
//        InOrder inOrder = inOrder(singleMock);
//
//        inOrder.verify(singleMock).add("was added first");
//        inOrder.verify(singleMock).add("was added second");
//
//        List firstMock = mock(List.class);
//        List secondMock = mock(List.class);
//
//        firstMock.add("was called first");
//        secondMock.add("was called second");
//
//        InOrder inOrder2 = inOrder(firstMock, secondMock);
//
//        inOrder2.verify(secondMock).add("was called second");
//        inOrder2.verify(firstMock).add("was called first");
//
//
//    }
//
//    private static class ValidMatch implements ArgumentMatcher {
//
//        @Override
//        public boolean matches(Object argument) {
//            return true;
//        }
//    }
//
//}
