package dev.syoritohatsuki.fstatsbackend.utils

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class SetupTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun initEnvironments() {
            SharedEnvironments
        }
    }
}