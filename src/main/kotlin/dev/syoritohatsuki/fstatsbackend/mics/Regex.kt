package dev.syoritohatsuki.fstatsbackend.mics

import org.intellij.lang.annotations.Language

@Language("RegExp")
const val USERNAME_REGEX = "^(\\w){3,16}$"

@Language("RegExp")
const val PROJECT_REGEX = "^[\\w\\s]{1,48}\$"

@Language("RegExp")
const val PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,64}\$"