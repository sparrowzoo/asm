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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.test.AsmTest;

/**
 * SerialVersionUIDAdder tests.
 *
 * @author Eric Bruneton
 */
public class SerialVersionUidAdderTest extends AsmTest {

  private long computeSerialVersionUid(final String className) throws IOException {
    long[] svuid = new long[1];
    new ClassReader(className)
        .accept(
            new SerialVersionUIDAdder(Opcodes.ASM7, null) {
              @Override
              protected long computeSVUID() throws IOException {
                svuid[0] = super.computeSVUID();
                return svuid[0];
              }
            },
            0);
    return svuid[0];
  }

  @Test
  public void testConstructor() {
    new SerialVersionUIDAdder(null);
    assertThrows(IllegalStateException.class, () -> new SerialVersionUIDAdder(null) {});
  }

  @Test
  public void testClass() throws IOException {
    long actualSvuid = computeSerialVersionUid(SerialVersionClass.class.getName());
    assertEquals(1084684350676694225L, actualSvuid);
  }

  @Test
  public void testAnonymousInnerClass() throws IOException {
    long actualSvuid =
        computeSerialVersionUid(SerialVersionAnonymousInnerClass.class.getName() + "$1");
    assertEquals(-1842070664294792585L, actualSvuid);
  }

  @Test
  public void testInterface() throws IOException {
    long actualSvuid = computeSerialVersionUid(SerialVersionInterface.class.getName());
    assertEquals(-3857402532274192875L, actualSvuid);
  }

  @Test
  public void testEmptyInterface() throws IOException {
    long actualSvuid = computeSerialVersionUid(SerialVersionEmptyInterface.class.getName());
    assertEquals(8675733916152748550L, actualSvuid);
  }

  @Test
  public void testEnum() throws IOException {
    long actualSvuid = computeSerialVersionUid(SerialVersionEnum.class.getName());
    assertEquals(0L, actualSvuid);
  }

  /**
   * Tests that SerialVersionUIDAdder succeeds on all precompiled classes, and that it actually adds
   * a serialVersionUID field.
   */
  @ParameterizedTest
  @MethodSource(ALL_CLASSES_AND_LATEST_API)
  public void testAddSerialVersionUid(
      final PrecompiledClass classParameter, final Api apiParameter) {
    ClassReader classReader = new ClassReader(classParameter.getBytes());
    ClassWriter classWriter = new ClassWriter(0);
    classReader.accept(new SerialVersionUIDAdder(classWriter), 0);
    if ((classReader.getAccess() & Opcodes.ACC_ENUM) == 0) {
      assertThatClass(classWriter.toByteArray()).contains("serialVersionUID");
    }
  }
}
