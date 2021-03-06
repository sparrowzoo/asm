// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package org.objectweb.asm.commons;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

/**
 * Method tests.
 *
 * @author Eric Bruneton
 */
public class MethodTest {

  @Test
  public void testConstructor1() {
    Method method = new Method("name", "(I)J");
    assertEquals("name", method.getName());
    assertEquals("(I)J", method.getDescriptor());
    assertEquals(Type.LONG_TYPE, method.getReturnType());
    assertArrayEquals(new Type[] {Type.INT_TYPE}, method.getArgumentTypes());
    assertEquals("name(I)J", method.toString());
  }

  @Test
  public void testConstructor2() {
    Method method = new Method("name", Type.LONG_TYPE, new Type[] {Type.INT_TYPE});
    assertEquals("name", method.getName());
    assertEquals("(I)J", method.getDescriptor());
    assertEquals(Type.LONG_TYPE, method.getReturnType());
    assertArrayEquals(new Type[] {Type.INT_TYPE}, method.getArgumentTypes());
    assertEquals("name(I)J", method.toString());
  }

  @Test
  public void testGetReflectMethod() throws NoSuchMethodException, SecurityException {
    Method method = Method.getMethod(Object.class.getMethod("equals", Object.class));
    assertEquals("equals", method.getName());
    assertEquals("(Ljava/lang/Object;)Z", method.getDescriptor());
  }

  @Test
  public void testGetReflectConstructor() throws NoSuchMethodException, SecurityException {
    Method method = Method.getMethod(Object.class.getConstructor());
    assertEquals("<init>", method.getName());
    assertEquals("()V", method.getDescriptor());
  }

  @Test
  public void testGetMethod() throws NoSuchMethodException, SecurityException {
    Method method =
        Method.getMethod(
            "boolean name(byte, char, short, int, float, long, double, pkg.Class, pkg.Class[])");
    assertEquals("name", method.getName());
    assertEquals("(BCSIFJDLpkg/Class;[Lpkg/Class;)Z", method.getDescriptor());

    assertThrows(IllegalArgumentException.class, () -> Method.getMethod("name()"));
    assertThrows(IllegalArgumentException.class, () -> Method.getMethod("void name"));
    assertThrows(IllegalArgumentException.class, () -> Method.getMethod("void name(]"));
  }

  @Test
  public void testGetMethodWithDefaultPackage() throws NoSuchMethodException, SecurityException {
    assertEquals(
        "(Ljava/lang/Object;)V",
        Method.getMethod("void name(Object)", /* defaultPackage= */ false).getDescriptor());
    assertEquals(
        "(LObject;)V",
        Method.getMethod("void name(Object)", /* defaultPackage= */ true).getDescriptor());
  }

  @Test
  public void testEquals() {
    assertNotEquals(new Method("name", "()V"), null);
    assertNotEquals(new Method("name", "()V"), new Method("other", "()V"));
    assertNotEquals(new Method("name", "()V"), new Method("name", "(I)J"));
    assertEquals(new Method("name", "()V"), Method.getMethod("void name()"));
  }

  @Test
  public void testHashCode() {
    assertNotEquals(0, new Method("name", "()V").hashCode());
  }
}
