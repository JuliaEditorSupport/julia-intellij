package org.ice1000.julia.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.ice1000.julia.lang.psi.JuliaTypes;

%%

STR=([^\"\x00-\x1F\x7F\]\|\[\'\"bnrt]|\\u[a-fA-F0-9]{4})*

%%

