/*
 * Copyright 2026 rusrusme
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.rusrusme.glaze.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class GBuiltinRegistry {
    private GBuiltinRegistry() {}

    public enum TypeKind {
        ANY_NUMERIC, // float, int, double, vec
        FLOAT,       // float, double
        VEC,         // vec2, vec3, vec4
        VEC3,        // vec3
        MAT,         // mat2, mat3, mat4
        QUAT         // quat
    }

    public record BuiltinSignature(
            @NotNull String name,
            @NotNull String tailText,
            @NotNull String returnType,
            @NotNull TypeKind[] expectedParams
    ) {}

    private static final Map<String, BuiltinSignature> BUILTINS;

    static {

        // Vektoren

        BUILTINS = Map.ofEntries(Map.entry("length", new BuiltinSignature("length", "(vec v)", "float", new TypeKind[]{TypeKind.VEC})), Map.entry("normalize", new BuiltinSignature("normalize", "(vec v)", "vec", new TypeKind[]{TypeKind.VEC})), Map.entry("distance", new BuiltinSignature("distance", "(vec a, vec b)", "float", new TypeKind[]{TypeKind.VEC, TypeKind.VEC})), Map.entry("dot", new BuiltinSignature("dot", "(vec a, vec b)", "float", new TypeKind[]{TypeKind.VEC, TypeKind.VEC})), Map.entry("cross", new BuiltinSignature("cross", "(vec3 a, vec3 b)", "vec3", new TypeKind[]{TypeKind.VEC3, TypeKind.VEC3})),

                // Matrizen
                Map.entry("inverse", new BuiltinSignature("inverse", "(mat m)", "mat", new TypeKind[]{TypeKind.MAT})), Map.entry("transpose", new BuiltinSignature("transpose", "(mat m)", "mat", new TypeKind[]{TypeKind.MAT})), Map.entry("determinant", new BuiltinSignature("determinant", "(mat m)", "float", new TypeKind[]{TypeKind.MAT})),

                // Quaternionen
                Map.entry("slerp", new BuiltinSignature("slerp", "(quat a, quat b, float t)", "quat", new TypeKind[]{TypeKind.QUAT, TypeKind.QUAT, TypeKind.FLOAT})), Map.entry("angleAxis", new BuiltinSignature("angleAxis", "(float angle, vec3 axis)", "quat", new TypeKind[]{TypeKind.FLOAT, TypeKind.VEC3})),

                // Interpolation & Bounds
                Map.entry("abs", new BuiltinSignature("abs", "(x)", "T", new TypeKind[]{TypeKind.ANY_NUMERIC})), Map.entry("sqrt", new BuiltinSignature("sqrt", "(float val)", "float", new TypeKind[]{TypeKind.FLOAT})), Map.entry("min", new BuiltinSignature("min", "(a, b)", "T", new TypeKind[]{TypeKind.ANY_NUMERIC, TypeKind.ANY_NUMERIC})), Map.entry("max", new BuiltinSignature("max", "(a, b)", "T", new TypeKind[]{TypeKind.ANY_NUMERIC, TypeKind.ANY_NUMERIC})), Map.entry("clamp", new BuiltinSignature("clamp", "(val, min, max)", "T", new TypeKind[]{TypeKind.ANY_NUMERIC, TypeKind.ANY_NUMERIC, TypeKind.ANY_NUMERIC})), Map.entry("mix", new BuiltinSignature("mix", "(a, b, float t)", "T", new TypeKind[]{TypeKind.ANY_NUMERIC, TypeKind.ANY_NUMERIC, TypeKind.FLOAT})),

                // Trigonometrie
                Map.entry("sin", new BuiltinSignature("sin", "(float rad)", "float", new TypeKind[]{TypeKind.FLOAT})), Map.entry("cos", new BuiltinSignature("cos", "(float rad)", "float", new TypeKind[]{TypeKind.FLOAT})), Map.entry("radians", new BuiltinSignature("radians", "(float deg)", "float", new TypeKind[]{TypeKind.FLOAT})), Map.entry("degrees", new BuiltinSignature("degrees", "(float rad)", "float", new TypeKind[]{TypeKind.FLOAT})));
    }

    public static boolean isBuiltin(@NotNull String name) {
        return BUILTINS.containsKey(name);
    }

    @Nullable
    public static BuiltinSignature getSignature(@NotNull String name) {
        return BUILTINS.get(name);
    }

    public static Map<String, BuiltinSignature> getAll() {
        return BUILTINS;
    }
}