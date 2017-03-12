package com.bee.reserve.utils;

import android.text.TextUtils;

import com.bee.reserve.datatypes.Customer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;

/**
 * Created by barisemreefe on 13/03/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class StringUtilsTest {
    @Before
    public void setup() {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence a = (CharSequence) invocation.getArguments()[0];
                return !(a != null && a.length() > 0);
            }
        });

    }
    @Test
    public void getCustomerName_null() throws Exception {
        assertThat(StringUtils.getCustomerName(null), isEmptyString());
    }

    public void getCustomerName_correct() throws Exception {
        assertThat(StringUtils.getCustomerName(new Customer("Baris","Efe",3)), is("Baris Efe"));
    }

    @Test
    public void getInitials_correct2() throws Exception {
        assertThat(StringUtils.getCustomerName(new Customer("Baris",null,3)), is("Baris"));
    }
    @Test
    public void getInitials_correct3() throws Exception {
        assertThat(StringUtils.getCustomerName(new Customer(null,"Efe",3)), is("Efe"));
    }



}