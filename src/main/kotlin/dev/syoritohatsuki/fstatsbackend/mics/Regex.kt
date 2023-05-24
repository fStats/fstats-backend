package dev.syoritohatsuki.fstatsbackend.mics

const val USERNAME_REGEX = "^([a-zA-Z0-9_]).{3,16}\$"
const val PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,64}\$"