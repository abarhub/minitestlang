package org.minitestlang.interpreter;

public sealed interface Value permits BoolValue, CharValue, IntValue, StringValue {
}
