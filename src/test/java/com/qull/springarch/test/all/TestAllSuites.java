package com.qull.springarch.test.all;

import com.qull.springarch.test.v1.V1TestAllSuites;
import com.qull.springarch.test.v2.V2TestAllSuites;
import com.qull.springarch.test.v3.V3TestAllSuites;
import com.qull.springarch.test.v4.V4TestAllSuites;
import com.qull.springarch.test.v5.V5TestAllSuites;
import com.qull.springarch.test.v6.V6TestAllSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 23:21
 */
@Suite.SuiteClasses({V1TestAllSuites.class, V2TestAllSuites.class, V3TestAllSuites.class, V4TestAllSuites.class, V5TestAllSuites.class, V6TestAllSuite.class})
@RunWith(Suite.class)
public class TestAllSuites {
}
