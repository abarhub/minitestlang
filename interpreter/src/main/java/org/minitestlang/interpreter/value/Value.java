package org.minitestlang.interpreter.value;

public sealed interface Value permits BoolValue, CharValue, IntValue, StringValue {
}
