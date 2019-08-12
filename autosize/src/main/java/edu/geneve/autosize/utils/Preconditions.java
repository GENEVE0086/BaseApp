/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.geneve.autosize.utils;

public class Preconditions {

  private Preconditions() {
    throw new IllegalStateException("Preconditions can not be instantiated!");
  }

  public static void checkArgument(boolean expression) {
    if (!expression) {
      throw new IllegalStateException();
    }
  }

  public static void checkArgument(boolean expression, Object errorMessage) {
    if (!expression) {
      throw new IllegalStateException(String.valueOf(errorMessage));
    }
  }

  public static void checkArgument(boolean expression,
      String errorMessageTemplate, Object... errorMessageArgs) {
    if (!expression) {
      throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
    }
  }

  public static void checkState(boolean expression) {
    if (!expression) {
      throw new IllegalStateException();
    }
  }

  public static void checkState(boolean expression, Object errorMessage) {
    if (!expression) {
      throw new IllegalStateException(String.valueOf(errorMessage));
    }
  }

  public static void checkState(boolean expression,
      String errorMessageTemplate, Object... errorMessageArgs) {
    if (!expression) {
      throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
    }
  }

  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    } else {
      return reference;
    }
  }

  public static <T> T checkNotNull(T reference, Object errorMessage) {
    if (reference == null) {
      throw new NullPointerException(String.valueOf(errorMessage));
    } else {
      return reference;
    }
  }

  public static <T> T checkNotNull(T reference,
      String errorMessageTemplate, Object... errorMessageArgs) {
    if (reference == null) {
      throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
    } else {
      return reference;
    }
  }

  public static int checkElementIndex(int index, int size) {
    return checkElementIndex(index, size, "index");
  }

  private static int checkElementIndex(int index, int size, String desc) {
    if (index >= 0 && index < size) {
      return index;
    } else {
      throw new IndexOutOfBoundsException(badElementIndex(index, size, desc));
    }
  }

  private static String badElementIndex(int index, int size, String desc) {
    if (index < 0) {
      return format("%s (%s) must not be negative", desc, index);
    } else if (size < 0) {
      throw new IllegalArgumentException("negative size: " + size);
    } else {
      return format("%s (%s) must be less than size (%s)", desc, index, size);
    }
  }

  public static int checkPositionIndex(int index, int size) {
    return checkPositionIndex(index, size, "index");
  }

  private static int checkPositionIndex(int index, int size, String desc) {
    if (index >= 0 && index <= size) {
      return index;
    } else {
      throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
    }
  }

  private static String badPositionIndex(int index, int size, String desc) {
    if (index < 0) {
      return format("%s (%s) must not be negative", desc, index);
    } else if (size < 0) {
      throw new IllegalArgumentException("negative size: " + size);
    } else {
      return format("%s (%s) must not be greater than size (%s)", desc, index, size);
    }
  }

  private static String format(String template, Object... args) {
    template = String.valueOf(template);
    StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
    int templateStart = 0;

    int i;
    int placeHolderStart;
    for (i = 0; i < args.length; templateStart = placeHolderStart + 2) {
      placeHolderStart = template.indexOf("%s", templateStart);
      if (placeHolderStart == -1) {
        break;
      }

      builder.append(template.substring(templateStart, placeHolderStart));
      builder.append(args[i++]);
    }

    builder.append(template.substring(templateStart));
    if (i < args.length) {
      builder.append("[");
      builder.append(args[i++]);

      while (i < args.length) {
        builder.append(", ");
        builder.append(args[i++]);
      }
      builder.append("]");
    }

    return builder.toString();
  }
}
