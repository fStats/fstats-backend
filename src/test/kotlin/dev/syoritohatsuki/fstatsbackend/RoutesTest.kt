package dev.syoritohatsuki.fstatsbackend

import dev.syoritohatsuki.fstatsbackend.routing.IndexTest
import dev.syoritohatsuki.fstatsbackend.routing.v3.*
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName
import dev.syoritohatsuki.fstatsbackend.routing.v2.MetricsRoutesTest as DeprecatedMetricsRoutesTest


@Suite
@SuiteDisplayName("fStats - Routes Test")
@SelectClasses(
    value = [
        IndexTest::class,
        AuthRoutesTest::class,
        UsersRoutesTest::class,
        ProjectsRoutesTest::class,
        FavoritesRoutesTest::class,
        DeprecatedMetricsRoutesTest::class,
        MetricsRoutesTest::class,
    ]
)
class RoutesTest