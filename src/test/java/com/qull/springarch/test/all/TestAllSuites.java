package com.qull.springarch.test.all;

import com.qull.springarch.test.v1.V1TestAllSuites;
import com.qull.springarch.test.v2.V2TestAllSuites;
import com.qull.springarch.test.v3.V3TestAllSuites;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 23:21
 */
@Suite.SuiteClasses({V1TestAllSuites.class, V2TestAllSuites.class, V3TestAllSuites.class})
@RunWith(Suite.class)
public class TestAllSuites {
}
