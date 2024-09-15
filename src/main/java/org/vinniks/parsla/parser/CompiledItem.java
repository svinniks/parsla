package org.vinniks.parsla.parser;

sealed interface CompiledItem permits CompiledRuleItem, AbstractCompiledTokenItem {
}
