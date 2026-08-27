/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassServer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Warps {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static ScriptValue canManage(ScriptContext.Builder builder) {
        boolean bl;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptContext.getClassOrVar("name"));
        if (!Warps.isOwner(builder2).asBool()) {
            Object object;
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("PERM_ADMIN_BYPASS");
                if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    object = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue2.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue2);
                    object = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            if (!object.asBool()) {
                bl = false;
                return ScriptValue.of((boolean)bl);
            }
        }
        bl = true;
        return ScriptValue.of((boolean)bl);
    }

    public static ScriptValue quit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$8_close_inventory());
            } else {
                ArrayList arrayList = new ArrayList();
                v0 = PolyDispatch.bootstrapCall("memberCall", "close_inventory", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue sanitize(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("s"));
        return ScriptFormula.callBuiltin((String)"trim", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue arrContains(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("arr"));
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                builder.val("x", scriptValue);
                if (!ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("x"), (ScriptValue)scriptContext.getClassOrVar("val"))) continue;
                return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 1);
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 0);
    }

    public static ScriptValue playerHeadIcon(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("name");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object3);
                object2 = polyClassServer.tm$10_get_player(scriptValue2.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("target", scriptValue3);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue3);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            Object object4;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("name"));
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Item");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "default:gui_head_size_1";
                double d = 1.0;
                if (scriptValue4 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Item")) {
                    PolyClassItem polyClassItem = new PolyClassItem(object5);
                    object4 = polyClassItem.tm$20_create(string, d);
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((String)string));
                    arrayList3.add(ScriptValue.of((double)d));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            return PolyDispatch.bootstrapCall("memberCall", "with_profile", (ScriptValue)object4, arrayList2, (ScriptContext)scriptContext);
        }
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(scriptValue3);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Item");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "default:gui_head_size_1";
            double d = 1.0;
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Item")) {
                PolyClassItem polyClassItem = new PolyClassItem(object6);
                object = polyClassItem.tm$20_create(string, d);
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((String)string));
                arrayList5.add(ScriptValue.of((double)d));
                object = PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return PolyDispatch.bootstrapCall("memberCall", "with_profile", (ScriptValue)object, arrayList4, (ScriptContext)scriptContext);
    }

    public static ScriptValue warpId(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE name = ?"));
            arrayList.add(scriptContext.getClassOrVar("name"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("rows", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "");
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("rows");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)object, arrayList2, (ScriptContext)scriptContext);
    }

    public static ScriptValue warpRow(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT * FROM warps WHERE name = ?"));
            arrayList.add(scriptContext.getClassOrVar("name"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("rows", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ArrayList arrayList2 = new ArrayList();
            return ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext);
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("rows");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue allWarpNames(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT name FROM warps ORDER BY created_at ASC"));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                Object object2;
                builder.val("row", scriptValue2);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("out"));
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("row");
                if (scriptValue3 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object2);
                ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
                builder.val("out", scriptValue4);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue warpExists(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warps WHERE name = ?"));
            arrayList2.add(scriptContext.getClassOrVar("name"));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        return ScriptValue.of((ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 ? 1 : 0) != 0);
    }

    public static ScriptValue ownedWarpNames(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT name FROM warps WHERE owner_uuid = ? ORDER BY created_at ASC"));
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            arrayList2.add((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                Object object2;
                builder.val("row", scriptValue3);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("out"));
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object2);
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
                builder.val("out", scriptValue5);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue isOwner(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "owner_uuid"));
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptContext.getClassOrVar("name"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        return ScriptValue.of((boolean)ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder2), arrayList, (ScriptContext)scriptContext), (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue createWarp(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        if (var1_1.getStr("name").equals("")) ** GOTO lbl-1000
        var2_2 = ScriptContext.builder().copyFrom(var1_1);
        var2_2.val("name", var1_1.getClassOrVar("name"));
        if (!Warps.warpExists(var2_2).asBool()) {
            v0 = false;
        } else lbl-1000:
        // 2 sources

        {
            v0 = true;
        }
        if (v0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 0);
        }
        var3_3 = var1_1.getClassOrVar("Player");
        var5_5 = var3_3 != ScriptValue.NULL ? ((var4_4 = PolyClassPlayer.ofGuarded((ScriptValue)var3_3)) != null ? var4_4.pg$73_location() : PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)var3_3, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var0.val("loc", var5_5);
        var6_6 = var1_1.getClassOrVar("SQL");
        if (var6_6 != ScriptValue.NULL) {
            var7_7 = new ArrayList<Object>();
            var7_7.add(ScriptValue.of((String)("INSERT INTO warps (id, name, owner_uuid, owner_name, world, x, y, z, created_at) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")));
            var8_8 = var1_1.getClassOrVar("Uuid");
            if (var8_8 != ScriptValue.NULL) {
                var9_9 = new ArrayList<E>();
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "random", (ScriptValue)var8_8, var9_9, (ScriptContext)var1_1);
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            var7_7.add(v1 /* !! */ );
            var7_7.add(var1_1.getClassOrVar("name"));
            var10_10 = var1_1.getClassOrVar("Player");
            var7_7.add(var10_10 != ScriptValue.NULL ? ((var11_11 = PolyClassPlayer.ofGuarded((ScriptValue)var10_10)) != null ? var11_11.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var10_10, (ScriptContext)var1_1)) : ScriptValue.NULL);
            var12_12 = var1_1.getClassOrVar("Player");
            var7_7.add(var12_12 != ScriptValue.NULL ? ((var13_13 = PolyClassPlayer.ofGuarded((ScriptValue)var12_12)) != null ? var13_13.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var12_12, (ScriptContext)var1_1)) : ScriptValue.NULL);
            var14_14 = var1_1.getClassOrVar("loc");
            var7_7.add(PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)(var14_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)var14_14, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
            var15_15 = var1_1.getClassOrVar("loc");
            var7_7.add(var15_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var15_15, (ScriptContext)var1_1) : ScriptValue.NULL);
            var16_16 = var1_1.getClassOrVar("loc");
            var7_7.add(var16_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var16_16, (ScriptContext)var1_1) : ScriptValue.NULL);
            var17_17 = var1_1.getClassOrVar("loc");
            var7_7.add(var17_17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var17_17, (ScriptContext)var1_1) : ScriptValue.NULL);
            var18_18 = var1_1.getClassOrVar("Server");
            var7_7.add(var18_18 != ScriptValue.NULL ? ((var19_19 = PolyClassServer.ofGuarded((ScriptValue)var18_18)) != null ? var19_19.pg$16_time() : PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)var18_18, (ScriptContext)var1_1)) : ScriptValue.NULL);
            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var6_6, var7_7, (ScriptContext)var1_1);
        } else {
            v2 /* !! */  = ScriptValue.NULL;
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 1);
    }

    public static ScriptValue deleteWarp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptContext.getClassOrVar("name"));
        if (Warps.isOwner(builder2).asBool() ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 0);
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE id = ?"));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("name", scriptContext.getClassOrVar("name"));
            arrayList.add(Warps.warpId(builder3));
            v0 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 1);
    }

    public static ScriptValue isBanned(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warp_bans WHERE warp_id = ? AND uuid = ?"));
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", scriptContext.getClassOrVar("name"));
            arrayList2.add(Warps.warpId(builder2));
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            arrayList2.add((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        return ScriptValue.of((ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 ? 1 : 0) != 0);
    }

    public static ScriptValue isFavourite(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warp_favourites WHERE warp_id = ? AND player_uuid = ?"));
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", scriptContext.getClassOrVar("name"));
            arrayList2.add(Warps.warpId(builder2));
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            arrayList2.add((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        return ScriptValue.of((ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 ? 1 : 0) != 0);
    }

    public static ScriptValue playerFavouriteIds(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT warp_id FROM warp_favourites WHERE player_uuid = ?"));
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            arrayList2.add((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                Object object2;
                builder.val("row", scriptValue3);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("out"));
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warp_id"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object2);
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
                builder.val("out", scriptValue5);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue playerBannedIds(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT warp_id FROM warp_bans WHERE uuid = ?"));
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            arrayList2.add((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                Object object2;
                builder.val("row", scriptValue3);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("out"));
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warp_id"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object2);
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
                builder.val("out", scriptValue5);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue recordVisit(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptContext.getClassOrVar("name"));
        ScriptValue scriptValue = Warps.warpId(builder2);
        builder.val("wid", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        if (scriptValue2 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT count FROM warp_visitors WHERE warp_id = ? AND uuid = ?"));
            arrayList.add(scriptValue);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            arrayList.add((ScriptValue)(scriptValue3 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("existing", scriptValue4);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue4);
        if (ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() > 0.0) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("SQL");
            if (scriptValue5 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer;
                PolyClassPlayer polyClassPlayer2;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warp_visitors SET count = count + 1, name = ? WHERE warp_id = ? AND uuid = ?"));
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
                arrayList2.add((ScriptValue)(scriptValue6 != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassPlayer2.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                arrayList2.add(scriptValue);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
                arrayList2.add((ScriptValue)(scriptValue7 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
            if (scriptValue8 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer;
                PolyClassPlayer polyClassPlayer3;
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_visitors (warp_id, uuid, name, count) VALUES (?, ?, ?, 1)"));
                arrayList3.add(scriptValue);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
                arrayList3.add((ScriptValue)(scriptValue9 != ScriptValue.NULL ? ((polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassPlayer3.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
                arrayList3.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, arrayList3, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue teleportToWarp(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = ScriptContext.builder().copyFrom(var1_1);
        var2_2.val("name", var1_1.getClassOrVar("name"));
        var3_3 = Warps.warpRow(var2_2);
        var0.val("data", var3_3);
        var4_4 = new ArrayList<ScriptValue>();
        var5_5 = var1_1.getClassOrVar("data");
        if (var5_5 != ScriptValue.NULL) {
            var6_6 = new ArrayList<ScriptValue>();
            var6_6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var5_5, var6_6, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var4_4.add(v0 /* !! */ );
        if (ScriptFormula.callBuiltin((String)"is_null", var4_4, (ScriptContext)var1_1).asBool()) {
            var7_7 = var1_1.getClassOrVar("Player");
            if (var7_7 != ScriptValue.NULL) {
                var8_8 = "<red>\u2718 <white>That warp no longer exists.";
                if (var7_7 instanceof ScriptValue.Obj && (var10_10 = (var9_9 = (ScriptValue.Obj)var7_7).instance()) != null && !(var10_10 instanceof PolyClass) && var9_9.typeName().equals("Player")) {
                    var11_11 = new PolyClassPlayer(var10_10);
                    v1 /* !! */  = ScriptValue.of((boolean)var11_11.tm$42_send_message(var8_8));
                } else {
                    var12_12 = new ArrayList<ScriptValue>();
                    var12_12.add(ScriptValue.of((String)var8_8));
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var7_7, var12_12, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var13_13 = new ArrayList<ScriptValue>();
        var14_14 = var1_1.getClassOrVar("SQL");
        if (var14_14 != ScriptValue.NULL) {
            var15_15 = new ArrayList<ScriptValue>();
            var15_15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warp_bans WHERE warp_id = ? AND uuid = ?"));
            var16_16 = var1_1.getClassOrVar("data");
            if (var16_16 != ScriptValue.NULL) {
                var17_17 = new ArrayList<ScriptValue>();
                var17_17.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var16_16, var17_17, (ScriptContext)var1_1);
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var15_15.add(v2 /* !! */ );
            var18_18 = var1_1.getClassOrVar("Player");
            var15_15.add((ScriptValue)(var18_18 != ScriptValue.NULL ? ((var19_19 = PolyClassPlayer.ofGuarded((ScriptValue)var18_18)) != null ? var19_19.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var18_18, (ScriptContext)var1_1)) : ScriptValue.NULL));
            v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)var14_14, var15_15, (ScriptContext)var1_1);
        } else {
            v3 /* !! */  = ScriptValue.NULL;
        }
        var13_13.add(v3 /* !! */ );
        if (ScriptFormula.callBuiltin((String)"len", var13_13, (ScriptContext)var1_1).asNum() > 0.0) {
            var20_20 = var1_1.getClassOrVar("Player");
            if (var20_20 != ScriptValue.NULL) {
                var21_21 = "<red>\u2718 <white>You are banned from that warp.";
                if (var20_20 instanceof ScriptValue.Obj && (var23_23 = (var22_22 = (ScriptValue.Obj)var20_20).instance()) != null && !(var23_23 instanceof PolyClass) && var22_22.typeName().equals("Player")) {
                    var24_24 = new PolyClassPlayer(var23_23);
                    v4 /* !! */  = ScriptValue.of((boolean)var24_24.tm$42_send_message(var21_21));
                } else {
                    var25_25 = new ArrayList<ScriptValue>();
                    var25_25.add(ScriptValue.of((String)var21_21));
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var20_20, var25_25, (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var26_26 = new ArrayList<ScriptValue>();
        var27_27 = var1_1.getClassOrVar("data");
        if (var27_27 != ScriptValue.NULL) {
            var28_28 = new ArrayList<ScriptValue>();
            var28_28.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "world"));
            v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var27_27, var28_28, (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        var26_26.add(v5 /* !! */ );
        var29_29 = ScriptFormula.callBuiltin((String)"world", var26_26, (ScriptContext)var1_1);
        var0.val("tw", var29_29);
        var30_30 = new ArrayList<ScriptValue>();
        var30_30.add(var29_29);
        if (ScriptFormula.callBuiltin((String)"is_empty", var30_30, (ScriptContext)var1_1).asBool()) {
            var31_31 = var1_1.getClassOrVar("Player");
            if (var31_31 != ScriptValue.NULL) {
                var32_32 = "<red>\u2718 <white>That warp's world is not currently loaded.";
                if (var31_31 instanceof ScriptValue.Obj && (var34_34 = (var33_33 = (ScriptValue.Obj)var31_31).instance()) != null && !(var34_34 instanceof PolyClass) && var33_33.typeName().equals("Player")) {
                    var35_35 = new PolyClassPlayer(var34_34);
                    v6 /* !! */  = ScriptValue.of((boolean)var35_35.tm$42_send_message(var32_32));
                } else {
                    var36_36 = new ArrayList<ScriptValue>();
                    var36_36.add(ScriptValue.of((String)var32_32));
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var31_31, var36_36, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var37_37 = var1_1.getClassOrVar("data");
        if (var37_37 != ScriptValue.NULL) {
            var38_38 = new ArrayList<ScriptValue>();
            var38_38.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "owner_uuid"));
            v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var37_37, var38_38, (ScriptContext)var1_1);
        } else {
            v7 /* !! */  = ScriptValue.NULL;
        }
        var39_39 = var1_1.getClassOrVar("Player");
        var41_41 = ScriptFormula.valuesEqual((ScriptValue)v7 /* !! */ , (ScriptValue)(var39_39 != ScriptValue.NULL ? ((var40_40 = PolyClassPlayer.ofGuarded((ScriptValue)var39_39)) != null ? var40_40.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var39_39, (ScriptContext)var1_1)) : ScriptValue.NULL));
        var42_42 = ScriptValue.of((boolean)var41_41);
        var0.val("is_own_warp", var42_42);
        if (!(var41_41 ^ true)) ** GOTO lbl-1000
        var43_43 = var1_1.getClassOrVar("Player");
        if (var43_43 != ScriptValue.NULL) {
            var44_44 = var1_1.getClassOrVar("WARP_TELEPORT_COST");
            if (var43_43 instanceof ScriptValue.Obj && (var46_46 = (var45_45 = (ScriptValue.Obj)var43_43).instance()) != null && !(var46_46 instanceof PolyClass) && var45_45.typeName().equals("Player")) {
                var47_47 = new PolyClassPlayer(var46_46);
                v8 /* !! */  = ScriptValue.of((boolean)var47_47.tm$28_take_exp(var44_44.asNum()));
            } else {
                var48_48 = new ArrayList<ScriptValue>();
                var48_48.add(var44_44);
                v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)var43_43, var48_48, (ScriptContext)var1_1);
            }
        } else {
            v8 /* !! */  = ScriptValue.NULL;
        }
        if (v8 /* !! */ .asBool() ^ true) {
            v9 = true;
        } else lbl-1000:
        // 2 sources

        {
            v9 = false;
        }
        if (v9) {
            var49_49 = var1_1.getClassOrVar("Player");
            if (var49_49 != ScriptValue.NULL) {
                var50_50 = ScriptValue.of((String)("<red>\u2718 <white>You need " + var1_1.getStr("WARP_TELEPORT_COST") + " XP points to use a warp."));
                if (var49_49 instanceof ScriptValue.Obj && (var52_52 = (var51_51 = (ScriptValue.Obj)var49_49).instance()) != null && !(var52_52 instanceof PolyClass) && var51_51.typeName().equals("Player")) {
                    var53_53 = new PolyClassPlayer(var52_52);
                    v10 /* !! */  = ScriptValue.of((boolean)var53_53.tm$42_send_message(var50_50.asStr()));
                } else {
                    var54_54 = new ArrayList<ScriptValue>();
                    var54_54.add(var50_50);
                    v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var49_49, var54_54, (ScriptContext)var1_1);
                }
            } else {
                v10 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var55_55 = var1_1.getClassOrVar("tw");
        if (var55_55 != ScriptValue.NULL) {
            var56_56 = new ArrayList<ScriptValue>();
            var57_57 = var1_1.getClassOrVar("data");
            if (var57_57 != ScriptValue.NULL) {
                var58_58 = new ArrayList<ScriptValue>();
                var58_58.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "x"));
                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var57_57, var58_58, (ScriptContext)var1_1);
            } else {
                v11 /* !! */  = ScriptValue.NULL;
            }
            var56_56.add(v11 /* !! */ );
            var59_59 = var1_1.getClassOrVar("data");
            if (var59_59 != ScriptValue.NULL) {
                var60_60 = new ArrayList<ScriptValue>();
                var60_60.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "y"));
                v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var59_59, var60_60, (ScriptContext)var1_1);
            } else {
                v12 /* !! */  = ScriptValue.NULL;
            }
            var56_56.add(v12 /* !! */ );
            var61_61 = var1_1.getClassOrVar("data");
            if (var61_61 != ScriptValue.NULL) {
                var62_62 = new ArrayList<ScriptValue>();
                var62_62.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "z"));
                v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var61_61, var62_62, (ScriptContext)var1_1);
            } else {
                v13 /* !! */  = ScriptValue.NULL;
            }
            var56_56.add(v13 /* !! */ );
            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var55_55, var56_56, (ScriptContext)var1_1);
        } else {
            v14 /* !! */  = ScriptValue.NULL;
        }
        var63_63 = v14 /* !! */ ;
        var0.val("loc", var63_63);
        var64_64 = var1_1.getClassOrVar("Player");
        if (var64_64 != ScriptValue.NULL) {
            var65_65 = var63_63;
            if (var64_64 instanceof ScriptValue.Obj && (var67_67 = (var66_66 = (ScriptValue.Obj)var64_64).instance()) != null && !(var67_67 instanceof PolyClass) && var66_66.typeName().equals("Player")) {
                var68_68 = new PolyClassPlayer(var67_67);
                v15 /* !! */  = ScriptValue.of((boolean)var68_68.tm$24_teleport_to(var65_65));
            } else {
                var69_69 = new ArrayList<ScriptValue>();
                var69_69.add(var65_65);
                v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var64_64, var69_69, (ScriptContext)var1_1);
            }
        } else {
            v15 /* !! */  = ScriptValue.NULL;
        }
        var70_70 = var1_1.getClassOrVar("SQL");
        if (var70_70 != ScriptValue.NULL) {
            var71_71 = new ArrayList<ScriptValue>();
            var71_71.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET visits = visits + 1 WHERE id = ?"));
            var72_72 = var1_1.getClassOrVar("data");
            if (var72_72 != ScriptValue.NULL) {
                var73_73 = new ArrayList<ScriptValue>();
                var73_73.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var72_72, var73_73, (ScriptContext)var1_1);
            } else {
                v16 /* !! */  = ScriptValue.NULL;
            }
            var71_71.add(v16 /* !! */ );
            v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var70_70, var71_71, (ScriptContext)var1_1);
        } else {
            v17 /* !! */  = ScriptValue.NULL;
        }
        var74_74 = ScriptContext.builder().copyFrom(var1_1);
        var74_74.val("name", var1_1.getClassOrVar("name"));
        Warps.recordVisit(var74_74);
        var75_75 = var1_1.getClassOrVar("Player");
        if (var75_75 != ScriptValue.NULL) {
            var76_76 = ScriptValue.of((String)("<green>\u2714 <white>Welcome to <yellow>" + var1_1.getStr("name") + "<white>!"));
            if (var75_75 instanceof ScriptValue.Obj && (var78_78 = (var77_77 = (ScriptValue.Obj)var75_75).instance()) != null && !(var78_78 instanceof PolyClass) && var77_77.typeName().equals("Player")) {
                var79_79 = new PolyClassPlayer(var78_78);
                v18 /* !! */  = ScriptValue.of((boolean)var79_79.tm$42_send_message(var76_76.asStr()));
            } else {
                var80_80 = new ArrayList<ScriptValue>();
                var80_80.add(var76_76);
                v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var75_75, var80_80, (ScriptContext)var1_1);
            }
        } else {
            v18 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue warpIconOf(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("row");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("icon", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"is_null", arrayList, (ScriptContext)scriptContext).asBool() || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "minecraft:ender_pearl");
        }
        return scriptValue2;
    }

    public static ScriptValue warpLoreOf(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("row");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "desc"));
            object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object5;
        builder.val("desc", scriptValue2);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("lore", (ScriptValue)array);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("lore"));
        StringBuilder stringBuilder = new StringBuilder().append("<gray>Owner: <white>");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("row");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "owner_name"));
            object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        arrayList2.add(ScriptValue.of((String)stringBuilder.append(object4.asStr()).toString()));
        ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"push", arrayList2, (ScriptContext)scriptContext);
        builder.val("lore", scriptValue4);
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(scriptContext.getClassOrVar("lore"));
        StringBuilder stringBuilder2 = new StringBuilder().append("<gray>World: <white>");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("row");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "world"));
            object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        arrayList4.add(ScriptValue.of((String)stringBuilder2.append(object3.asStr()).toString()));
        ScriptValue scriptValue6 = ScriptFormula.callBuiltin((String)"push", arrayList4, (ScriptContext)scriptContext);
        builder.val("lore", scriptValue6);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(scriptContext.getClassOrVar("lore"));
        StringBuilder stringBuilder3 = new StringBuilder().append("<gray>Visits: <white>");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visits"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList7, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList6.add(ScriptValue.of((String)stringBuilder3.append(object2.asStr()).toString()));
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin((String)"push", arrayList6, (ScriptContext)scriptContext);
        builder.val("lore", scriptValue8);
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add(scriptContext.getClassOrVar("lore"));
        StringBuilder stringBuilder4 = new StringBuilder().append("<gray>Category: <white>");
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
            arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "category"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList9, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList8.add(ScriptValue.of((String)stringBuilder4.append(object.asStr()).toString()));
        ScriptValue scriptValue10 = ScriptFormula.callBuiltin((String)"push", arrayList8, (ScriptContext)scriptContext);
        builder.val("lore", scriptValue10);
        ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
        arrayList10.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"is_null", arrayList10, (ScriptContext)scriptContext).asBool() ^ true && ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true) {
            ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
            arrayList11.add(scriptContext.getClassOrVar("lore"));
            arrayList11.add(ScriptValue.of((String)("<gray>Description: <white>" + scriptValue2.asStr())));
            ScriptValue scriptValue11 = ScriptFormula.callBuiltin((String)"push", arrayList11, (ScriptContext)scriptContext);
            builder.val("lore", scriptValue11);
        }
        if (scriptContext.getBool("is_fav")) {
            ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
            arrayList12.add(scriptContext.getClassOrVar("lore"));
            arrayList12.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<gold>\u2605 Favourited"));
            ScriptValue scriptValue12 = ScriptFormula.callBuiltin((String)"push", arrayList12, (ScriptContext)scriptContext);
            builder.val("lore", scriptValue12);
        }
        if (scriptContext.getBool("is_ban")) {
            ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
            arrayList13.add(scriptContext.getClassOrVar("lore"));
            arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>You are banned from this warp"));
            ScriptValue scriptValue13 = ScriptFormula.callBuiltin((String)"push", arrayList13, (ScriptContext)scriptContext);
            builder.val("lore", scriptValue13);
        }
        ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
        arrayList14.add(scriptContext.getClassOrVar("lore"));
        arrayList14.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
        ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"push", arrayList14, (ScriptContext)scriptContext);
        builder.val("lore", scriptValue14);
        ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
        arrayList15.add(scriptContext.getClassOrVar("lore"));
        arrayList15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<green>Left-click <gray>to teleport"));
        ScriptValue scriptValue15 = ScriptFormula.callBuiltin((String)"push", arrayList15, (ScriptContext)scriptContext);
        builder.val("lore", scriptValue15);
        ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
        arrayList16.add(scriptContext.getClassOrVar("lore"));
        arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<yellow>Right-click <gray>for more options"));
        ScriptValue scriptValue16 = ScriptFormula.callBuiltin((String)"push", arrayList16, (ScriptContext)scriptContext);
        builder.val("lore", scriptValue16);
        return scriptValue16;
    }

    public static ScriptValue warpIcon(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptContext.getClassOrVar("name"));
        builder2.val("row", Warps.warpRow(builder3));
        return Warps.warpIconOf(builder2);
    }

    public static ScriptValue warpLore(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptContext.getClassOrVar("name"));
        builder2.val("row", Warps.warpRow(builder3));
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptContext.getClassOrVar("name"));
        builder2.val("is_fav", Warps.isFavourite(builder4));
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        builder5.val("name", scriptContext.getClassOrVar("name"));
        builder2.val("is_ban", Warps.isBanned(builder5));
        return Warps.warpLoreOf(builder2);
    }

    public static ScriptValue sortOrderClause(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_sort";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("mode", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"name")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name ASC");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"visits")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visits DESC");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "created_at ASC");
    }

    public static ScriptValue sortLabel(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_sort";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("mode", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"name")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Alphabetical");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"visits")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Most Visited");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Oldest");
    }

    public static ScriptValue cycleSort(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_sort";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("mode", scriptValue2);
        double d = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue3);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("SORT_MODES"));
        arrayList.add(ScriptFormula.callBuiltin((String)"len", arrayList2, (ScriptContext)scriptContext));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                Object object3;
                builder.val("i", scriptValue4);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("SORT_MODES");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptContext.getClassOrVar("i"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                if (!ScriptFormula.valuesEqual((ScriptValue)object3, (ScriptValue)scriptValue2)) continue;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("i");
                builder.val("idx", scriptValue6);
            }
        }
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(scriptContext.getClassOrVar("SORT_MODES"));
        double d2 = ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum();
        double d3 = d2 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0))).asNum() % d2;
        ScriptValue scriptValue7 = ScriptValue.of((double)d3);
        builder.val("next_idx", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue9;
            String string = "warps_sort";
            String string3 = "string";
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("SORT_MODES");
            if (scriptValue10 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((double)d3));
                v2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, arrayList5, (ScriptContext)scriptContext);
            } else {
                v2 = scriptValue9 = ScriptValue.NULL;
            }
            if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue9));
            } else {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(ScriptValue.of((String)string));
                arrayList6.add(ScriptValue.of((String)string3));
                arrayList6.add(scriptValue9);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, arrayList6, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue11 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add(scriptContext.getClassOrVar("page_name"));
            v4 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue11, arrayList7, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue openSearchDialog(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search"));
        arrayList2.add(ScriptValue.of((String)("warps.pf:on_search_submit:" + scriptContext.getStr("page_name"))));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Warp or owner name"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_search";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                arrayList4.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList3.add((ScriptValue)object2);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search Warps"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onSearchSubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_search";
            String string2 = "string";
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("s", scriptContext.getClassOrVar("value"));
            ScriptValue scriptValue2 = Warps.sanitize(builder2);
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_browse_offset";
            String string3 = "int";
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue4));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue4);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_manage_offset";
            String string4 = "int";
            ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string4, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string4));
                arrayList.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_favourite_offset";
            String string5 = "int";
            ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string5, scriptValue8));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string5));
                arrayList.add(scriptValue8);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("page_name"));
            v4 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue clearSearch(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_search";
            String string2 = "string";
            ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "");
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("page_name"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue openCategory(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_category_filter";
            String string2 = "string";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("cat");
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_browse_offset";
            String string3 = "int";
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue4));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue4);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cycleCategory(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_category_filter";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("filter", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "all");
            builder.val("filter", scriptValue3);
        }
        double d = 0.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue4);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("CATEGORIES"));
        arrayList.add(ScriptFormula.callBuiltin((String)"len", arrayList2, (ScriptContext)scriptContext));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                Object object3;
                builder.val("i", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("CATEGORIES");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptContext.getClassOrVar("i"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList3, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                if (!ScriptFormula.valuesEqual((ScriptValue)object3, (ScriptValue)scriptContext.getClassOrVar("filter"))) continue;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("i");
                builder.val("idx", scriptValue7);
            }
        }
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(scriptContext.getClassOrVar("CATEGORIES"));
        double d2 = ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum();
        double d3 = d2 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0))).asNum() % d2;
        ScriptValue scriptValue8 = ScriptValue.of((double)d3);
        builder.val("next_idx", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue10;
            String string = "warps_category_filter";
            String string3 = "string";
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("CATEGORIES");
            if (scriptValue11 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((double)d3));
                v2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList5, (ScriptContext)scriptContext);
            } else {
                v2 = scriptValue10 = ScriptValue.NULL;
            }
            if (scriptValue9 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue10));
            } else {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(ScriptValue.of((String)string));
                arrayList6.add(ScriptValue.of((String)string3));
                arrayList6.add(scriptValue10);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, arrayList6, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "warps_browse_offset";
            String string4 = "int";
            ScriptValue scriptValue13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue12 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string4, scriptValue13));
            } else {
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(ScriptValue.of((String)string));
                arrayList7.add(ScriptValue.of((String)string4));
                arrayList7.add(scriptValue13);
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, arrayList7, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue14 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
            arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps"));
            v5 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue14, arrayList8, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue categoryLabel(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_category_filter";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("filter", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"houses")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Houses");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"shops")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Shops");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"farms")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Farms");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"pvp")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "PvP");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"other")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Other");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"event")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Event");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "All");
    }

    public static ScriptValue browseTitle(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_browse_offset";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("offset", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("{Images.from('cml:player_warps_warps')}{Images.shift(-170)}<black> Page " + ScriptFormula.addPolymorphic((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0))).asStr() + "/" + ScriptFormula.addPolymorphic((ScriptValue)Warps.maxBrowseOffset(builder2), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0))).asStr()));
    }

    public static ScriptValue filteredBrowseRows(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "warps_category_filter";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object3 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("filter", scriptValue2);
        StringBuilder stringBuilder = new StringBuilder().append("%");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "warps_search";
            String string3 = "string";
            if (scriptValue3 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                object2 = polyClassPlayer.tm$12_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string3));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object2);
        ScriptValue scriptValue4 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"lower", arrayList, (ScriptContext)scriptContext).asStr()).append("%").toString());
        builder.val("like", scriptValue4);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = Warps.sortOrderClause(builder2);
        builder.val("order", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"all")) {
            Object object6;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)("SELECT * FROM warps WHERE locked = 0 AND (lower(name) LIKE ? OR lower(owner_name) LIKE ?) ORDER BY " + scriptValue5.asStr())));
                arrayList3.add(scriptValue4);
                arrayList3.add(scriptValue4);
                object6 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue6, arrayList3, (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            return object6;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("SQL");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((String)("SELECT * FROM warps WHERE locked = 0 AND category = ? AND (lower(name) LIKE ? OR lower(owner_name) LIKE ?) ORDER BY " + scriptValue5.asStr())));
            arrayList4.add(scriptValue2);
            arrayList4.add(scriptValue4);
            arrayList4.add(scriptValue4);
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue maxBrowseOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.filteredBrowseRows(builder2));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("WARP_PAGE_SIZE_BROWSE");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
    }

    public static ScriptValue generateBrowseButtons(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_browse_offset";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("offset", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue3 = Warps.filteredBrowseRows(builder2);
        builder.val("rows", scriptValue3);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue4 = Warps.playerFavouriteIds(builder3);
        builder.val("fav_ids", scriptValue4);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = Warps.playerBannedIds(builder4);
        builder.val("ban_ids", scriptValue5);
        double d = scriptValue2.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_BROWSE");
        ScriptValue scriptValue6 = ScriptValue.of((double)d);
        builder.val("start", scriptValue6);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        arrayList3.add(scriptContext.getClassOrVar("WARP_PAGE_SIZE_BROWSE"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue7 : list) {
                Object object3;
                Object object4;
                Object object5;
                Object object6;
                Object object7;
                builder.val("i", scriptValue7);
                ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue8);
                double d2 = scriptValue8.asNum();
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue3);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("rows");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue8);
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object7;
                builder.val("row", scriptValue10);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("row");
                if (scriptValue11 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = object6;
                builder.val("name", scriptValue12);
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("WARP_SLOTS_BROWSE");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(scriptContext.getClassOrVar("i"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList9, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                arrayList8.add((ScriptValue)object5);
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("row", scriptValue10);
                arrayList8.add(Warps.warpIconOf(builder5));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList8.add(ScriptValue.of((String)("<yellow>" + scriptValue12.asStr())));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("row", scriptValue10);
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("arr", scriptValue4);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("row");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, arrayList10, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                builder7.val("val", object4);
                builder6.val("is_fav", Warps.arrContains(builder7));
                ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                builder8.val("arr", scriptValue5);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("row");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, arrayList11, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                builder8.val("val", object3);
                builder6.val("is_ban", Warps.arrContains(builder8));
                arrayList8.add(Warps.warpLoreOf(builder6));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList8.add(ScriptValue.of((String)("warps.pf:on_browse_click:" + scriptValue12.asStr())));
                arrayList7.add(ScriptFormula.callBuiltin((String)"make_map", arrayList8, (ScriptContext)scriptContext));
                ScriptValue scriptValue16 = ScriptFormula.callBuiltin((String)"push", arrayList7, (ScriptContext)scriptContext);
                builder.val("out", scriptValue16);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue onBrowseClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("MenuClick");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "click_type", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"right")) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "warps_current_warp";
                String string2 = "string";
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("name");
                if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue3));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    arrayList.add(scriptValue3);
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "warps_return_page";
                String string3 = "string";
                ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps");
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visitedit"));
                v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", scriptContext.getClassOrVar("name"));
            Warps.teleportToWarp(builder2);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue prevBrowsePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_browse_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_browse_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptValue.of((double)(scriptValue3.asNum() - 1.0));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue nextBrowsePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_browse_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        if (scriptValue3.asNum() < Warps.maxBrowseOffset(builder2).asNum()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_browse_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue effectiveLimitNum(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("WARP_SPONSOR_PERMISSION");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                object2 = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 999999.0);
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("WARP_FREE_LIMIT");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "warps_extra_slots";
            String string2 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)object);
    }

    public static ScriptValue effectiveLimit(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("WARP_SPONSOR_PERMISSION");
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                object = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "\u221e");
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return Warps.effectiveLimitNum(builder2);
    }

    public static ScriptValue buyExtraSlot(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("WARP_EXTRA_SLOT_COST");
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$28_take_exp(scriptValue2.asNum()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                object = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>You need " + scriptContext.getStr("WARP_EXTRA_SLOT_COST") + " XP points to buy an extra warp slot."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            Object object5;
            String string = "warps_extra_slots";
            String string2 = "int";
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object6;
                String string3 = "warps_extra_slots";
                String string4 = "int";
                if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj2 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj2.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                    object5 = polyClassPlayer.tm$12_get_typed(string3, string4);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(ScriptValue.of((String)string4));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)object5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
            if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue7));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue7);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            StringBuilder stringBuilder = new StringBuilder().append("<green>\u2714 <white>Unlocked an extra warp slot! (");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            arrayList.add(Warps.ownedWarpNames(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue9 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asStr()).append("/").append(Warps.effectiveLimit(builder3).asStr()).append(" used)").toString());
            if (scriptValue8 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue9.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue9);
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mywarpsCountLine(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        StringBuilder stringBuilder = new StringBuilder().append("<gray>Current: <white>");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.ownedWarpNames(builder2));
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asStr()).append("/").append(Warps.effectiveLimit(builder3).asStr()).toString());
    }

    public static ScriptValue categoryLoreLine(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("<gray>Current: <white>" + Warps.categoryLabel(builder2).asStr()));
    }

    public static ScriptValue sortLoreLine(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("<gray>Current: <white>" + Warps.sortLabel(builder2).asStr()));
    }

    public static ScriptValue onCreateCommand(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        builder2.val("name", object);
        Warps.onCreateSubmit(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue onSetCommand(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        builder2.val("s", object);
        ScriptValue scriptValue2 = Warps.sanitize(builder2);
        builder.val("name", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<red>\u2718 <white>Warp name cannot be empty.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.warpExists(builder3).asBool()) {
            PolyClassPlayer polyClassPlayer;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            if (Warps.isOwner(builder4).asBool() ^ true) {
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
                if (scriptValue4 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object3;
                    ScriptValue scriptValue5 = ScriptValue.of((String)("<red>\u2718 <white>A warp named '" + scriptValue2.asStr() + "' already exists."));
                    if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object3);
                        v2 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue5.asStr()));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue5);
                        v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v2 = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassPlayer.pg$73_location() : PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("loc", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
            if (scriptValue8 != ScriptValue.NULL) {
                ArrayList<Object> arrayList = new ArrayList<Object>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET world = ?, x = ?, y = ?, z = ? WHERE id = ?"));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("loc");
                arrayList.add(PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("loc");
                arrayList.add(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("loc");
                arrayList.add(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("loc");
                arrayList.add(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("name", scriptValue2);
                arrayList.add(Warps.warpId(builder5));
                v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue14 = ScriptValue.of((String)("<green>\u2714 <white>Moved '" + scriptValue2.asStr() + "' to your current location."));
                if (scriptValue13 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object4);
                    v4 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(scriptValue14.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue14);
                    v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
        builder6.val("name", scriptValue2);
        Warps.onCreateSubmit(builder6);
        return ScriptValue.NULL;
    }

    public static ScriptValue onCreateSubmit(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("s", scriptContext.getClassOrVar("name"));
        ScriptValue scriptValue = Warps.sanitize(builder2);
        builder.val("name", scriptValue);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"")) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<red>\u2718 <white>Warp name cannot be empty.";
                if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.warpExists(builder3).asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>A warp named '" + scriptValue.asStr() + "' already exists."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.ownedWarpNames(builder4));
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        if (ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() >= Warps.effectiveLimitNum(builder5).asNum()) {
            Object object4;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("WARP_EXTRA_SLOT_COST");
                if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                    object4 = ScriptValue.of((boolean)polyClassPlayer.tm$38_has_exp(scriptValue6.asNum()));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptValue6);
                    object4 = PolyDispatch.bootstrapCall("memberCall", "has_exp", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            if (object4.asBool()) {
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
                if (scriptValue7 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object6;
                    ScriptValue scriptValue8 = ScriptValue.of((String)("<red>\u2718 <white>You've reached your warp limit. <yellow>You have enough XP to unlock another - try <white>/warps buyslot<yellow>, then create '" + scriptValue.asStr() + "' again."));
                    if (scriptValue7 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                        v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue8.asStr()));
                    } else {
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        arrayList3.add(scriptValue8);
                        v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList3, (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
                if (scriptValue9 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object7;
                    String string = "<red>\u2718 <white>You've reached your warp limit.";
                    if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                        v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                    } else {
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        arrayList4.add(ScriptValue.of((String)string));
                        v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, arrayList4, (ScriptContext)scriptContext);
                    }
                } else {
                    v4 = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("WARP_CREATE_COST");
            if (scriptValue10 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$38_has_exp(scriptValue11.asNum()));
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue11);
                object = PolyDispatch.bootstrapCall("memberCall", "has_exp", (ScriptValue)scriptValue10, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                ScriptValue scriptValue13 = ScriptValue.of((String)("<red>\u2718 <white>You need " + scriptContext.getStr("WARP_CREATE_COST") + " XP points to create a warp."));
                if (scriptValue12 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object9);
                    v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue13.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(scriptValue13);
                    v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, arrayList6, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
        builder6.val("name", scriptValue);
        if (Warps.createWarp(builder6).asBool()) {
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("WARP_CREATE_COST");
                if (scriptValue14 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object10);
                    v7 = ScriptValue.of((boolean)polyClassPlayer.tm$28_take_exp(scriptValue15.asNum()));
                } else {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(scriptValue15);
                    v7 = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)scriptValue14, arrayList7, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                ScriptValue scriptValue17 = ScriptValue.of((String)("<green>\u2714 <white>Warp '" + scriptValue.asStr() + "' created for " + scriptContext.getStr("WARP_CREATE_COST") + " XP!"));
                if (scriptValue16 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object11);
                    v8 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue17.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptValue17);
                    v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, arrayList8, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("Player");
            if (scriptValue18 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                String string = "<red>\u2718 <white>Could not create that warp.";
                if (scriptValue18 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object12);
                    v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(ScriptValue.of((String)string));
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue18, arrayList9, (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue purgeExpiredSponsors(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            PolyClassServer polyClassServer;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_sponsors WHERE expires_at < ?"));
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Server");
            arrayList.add((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassServer.pg$16_time() : PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            v0 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue sponsorRow(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        Warps.purgeExpiredSponsors(builder2);
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)("SELECT w.name AS name, s.buyer_uuid AS buyer_uuid, s.expires_at AS expires_at " + "FROM warp_sponsors s JOIN warps w ON w.id = s.warp_id WHERE s.slot_num = ?")));
            arrayList.add(scriptContext.getClassOrVar("slot_num"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("rows", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ArrayList arrayList2 = new ArrayList();
            return ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext);
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("rows");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue generateSponsorButtons(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(scriptContext.getClassOrVar("SPONSOR_SLOTS"));
        arrayList2.add(ScriptFormula.callBuiltin((String)"len", arrayList3, (ScriptContext)scriptContext));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList2, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                PolyClassPlayer polyClassPlayer;
                Object object;
                Object object2;
                Object object3;
                builder.val("i", scriptValue);
                ScriptValue scriptValue2 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                builder.val("slot_num", scriptValue2);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("SPONSOR_SLOTS");
                if (scriptValue3 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(scriptContext.getClassOrVar("i"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue4 = object3;
                builder.val("slot", scriptValue4);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("slot_num", scriptValue2);
                ScriptValue scriptValue5 = Warps.sponsorRow(builder2);
                builder.val("row", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("row");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object2;
                builder.val("name", scriptValue7);
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptValue7);
                if (ScriptFormula.callBuiltin((String)"is_null", arrayList6, (ScriptContext)scriptContext).asBool()) {
                    double d;
                    Object object4;
                    ScriptValue scriptValue8 = scriptContext.getClassOrVar("SPONSOR_PRICES");
                    if (scriptValue8 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                        arrayList7.add(scriptContext.getClassOrVar("i"));
                        object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, arrayList7, (ScriptContext)scriptContext);
                    } else {
                        object4 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue9 = object4;
                    builder.val("price", scriptValue9);
                    double d2 = 3600.0;
                    if (3600.0 == 0.0) {
                        d = 0.0;
                    } else {
                        Object object5;
                        ScriptValue scriptValue10 = scriptContext.getClassOrVar("SPONSOR_DURATIONS");
                        if (scriptValue10 != ScriptValue.NULL) {
                            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                            arrayList8.add(scriptContext.getClassOrVar("i"));
                            object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, arrayList8, (ScriptContext)scriptContext);
                        } else {
                            object5 = ScriptValue.NULL;
                        }
                        d = object5.asNum() / d2;
                    }
                    double d3 = d;
                    ScriptValue scriptValue11 = ScriptValue.of((double)d);
                    builder.val("hours", scriptValue11);
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(scriptContext.getClassOrVar("out"));
                    ArrayList<Object> arrayList10 = new ArrayList<Object>();
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                    arrayList10.add(scriptValue4);
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "minecraft:diamond"));
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    arrayList10.add(ScriptValue.of((String)("<yellow>Sponsor Slot #" + scriptValue2.asStr())));
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<gray>Feature one of YOUR warps here."));
                    arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                    arrayList11.add(ScriptValue.of((String)("<gray>Price: <white>" + scriptValue9.asStr() + " XP")));
                    arrayList11.add(ScriptValue.of((String)("<gray>Time: <white>" + ScriptFormula.numToStr((double)d3) + "h")));
                    arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                    arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<green>Left-click to purchase"));
                    arrayList10.add(new ScriptValue.Array(arrayList11));
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                    arrayList10.add(ScriptValue.of((String)("warps.pf:open_sponsor_dialog:" + scriptValue2.asStr())));
                    arrayList9.add(ScriptFormula.callBuiltin((String)"make_map", arrayList10, (ScriptContext)scriptContext));
                    ScriptValue scriptValue12 = ScriptFormula.callBuiltin((String)"push", arrayList9, (ScriptContext)scriptContext);
                    builder.val("out", scriptValue12);
                    continue;
                }
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue7);
                ScriptValue scriptValue13 = Warps.warpLore(builder3);
                builder.val("lore", scriptValue13);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("row");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    arrayList12.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "buyer_uuid"));
                    object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, arrayList12, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                Object object6 = scriptValue15 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                if (ScriptFormula.valuesEqual((ScriptValue)object, (ScriptValue)object6)) {
                    ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                    arrayList13.add(scriptContext.getClassOrVar("lore"));
                    arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                    ScriptValue scriptValue16 = ScriptFormula.callBuiltin((String)"push", arrayList13, (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue16);
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add(scriptContext.getClassOrVar("lore"));
                    arrayList14.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>Shift-right-click to remove your sponsor"));
                    ScriptValue scriptValue17 = ScriptFormula.callBuiltin((String)"push", arrayList14, (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue17);
                }
                ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                arrayList15.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                arrayList16.add(scriptValue4);
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("name", scriptValue7);
                arrayList16.add(Warps.warpIcon(builder4));
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList16.add(ScriptValue.of((String)("<gold>[Sponsored] <yellow>" + scriptValue7.asStr())));
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                arrayList16.add(scriptContext.getClassOrVar("lore"));
                arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList16.add(ScriptValue.of((String)("warps.pf:on_sponsor_click:" + scriptValue2.asStr())));
                arrayList15.add(ScriptFormula.callBuiltin((String)"make_map", arrayList16, (ScriptContext)scriptContext));
                ScriptValue scriptValue18 = ScriptFormula.callBuiltin((String)"push", arrayList15, (ScriptContext)scriptContext);
                builder.val("out", scriptValue18);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue openSponsorDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Sponsor"));
        arrayList2.add(ScriptValue.of((String)("warps.pf:on_sponsor_submit:" + scriptContext.getStr("slot_num"))));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "One of your warp names"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((String)("Sponsor Slot #" + scriptContext.getStr("slot_num"))));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onSponsorSubmit(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("s", scriptContext.getClassOrVar("value"));
        ScriptValue scriptValue = Warps.sanitize(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue3 = ScriptValue.of((String)("<red>\u2718 <white>You don't own a warp named '" + scriptValue.asStr() + "'."));
                if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue3.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue3);
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("slot_num"));
        double d = ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext).asNum() - 1.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("idx", scriptValue4);
        ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(scriptContext.getClassOrVar("slot_num"));
        builder4.val("slot_num", ScriptFormula.callBuiltin((String)"int", arrayList4, (ScriptContext)scriptContext));
        arrayList2.add(PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.sponsorRow(builder4), arrayList3, (ScriptContext)scriptContext));
        if (ScriptFormula.callBuiltin((String)"is_null", arrayList2, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>That sponsor slot is already taken.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SPONSOR_PRICES");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            arrayList6.add(ScriptValue.of((double)d));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList6, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object2;
        builder.val("price", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue9 = scriptValue7;
            if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$38_has_exp(scriptValue9.asNum()));
            } else {
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptValue9);
                object = PolyDispatch.bootstrapCall("memberCall", "has_exp", (ScriptValue)scriptValue8, arrayList7, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptValue scriptValue11 = ScriptValue.of((String)("<red>\u2718 <white>You need " + scriptValue7.asStr() + " XP to sponsor slot #" + scriptContext.getStr("slot_num") + "."));
                if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                    v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptValue11);
                    v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList8, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptValue scriptValue13 = scriptValue7;
            if (scriptValue12 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$28_take_exp(scriptValue13.asNum()));
            } else {
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(scriptValue13);
                v5 = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)scriptValue12, arrayList9, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("SQL");
        if (scriptValue14 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
            arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_sponsors WHERE slot_num = ?"));
            ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
            arrayList11.add(scriptContext.getClassOrVar("slot_num"));
            arrayList10.add(ScriptFormula.callBuiltin((String)"int", arrayList11, (ScriptContext)scriptContext));
            v6 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue14, arrayList10, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("SQL");
        if (scriptValue15 != ScriptValue.NULL) {
            Object object8;
            PolyClassServer polyClassServer;
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
            arrayList12.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_sponsors (slot_num, warp_id, buyer_uuid, expires_at) VALUES (?, ?, ?, ?)"));
            ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
            arrayList13.add(scriptContext.getClassOrVar("slot_num"));
            arrayList12.add(ScriptFormula.callBuiltin((String)"int", arrayList13, (ScriptContext)scriptContext));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue);
            arrayList12.add(Warps.warpId(builder5));
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
            arrayList12.add((ScriptValue)(scriptValue16 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Server");
            Object object9 = scriptValue17 != ScriptValue.NULL ? ((polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue17)) != null ? polyClassServer.pg$16_time() : PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue17, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("SPONSOR_DURATIONS");
            if (scriptValue18 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                arrayList14.add(ScriptValue.of((double)d));
                object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList14, (ScriptContext)scriptContext);
            } else {
                object8 = ScriptValue.NULL;
            }
            arrayList12.add(ScriptFormula.addPolymorphic((ScriptValue)object9, (ScriptValue)object8));
            v9 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue15, arrayList12, (ScriptContext)scriptContext);
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Player");
        if (scriptValue19 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object10;
            ScriptValue scriptValue20 = ScriptValue.of((String)("<green>\u2714 <white>Sponsored '" + scriptValue.asStr() + "' on slot #" + scriptContext.getStr("slot_num") + "!"));
            if (scriptValue19 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object10);
                v10 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue20.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                arrayList15.add(scriptValue20);
                v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, arrayList15, (ScriptContext)scriptContext);
            }
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue21 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
            arrayList16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "main"));
            v11 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue21, arrayList16, (ScriptContext)scriptContext);
        } else {
            v11 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue onSponsorClick(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = ScriptContext.builder().copyFrom(var1_1);
        var3_3 = new ArrayList<ScriptValue>();
        var3_3.add(var1_1.getClassOrVar("slot_num"));
        var2_2.val("slot_num", ScriptFormula.callBuiltin((String)"int", var3_3, (ScriptContext)var1_1));
        var4_4 = Warps.sponsorRow(var2_2);
        var0.val("row", var4_4);
        var5_5 = var1_1.getClassOrVar("row");
        if (var5_5 != ScriptValue.NULL) {
            var6_6 = new ArrayList<ScriptValue>();
            var6_6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var5_5, var6_6, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var7_7 = v0 /* !! */ ;
        var0.val("name", var7_7);
        var8_8 = new ArrayList<ScriptValue>();
        var8_8.add(var7_7);
        if (ScriptFormula.callBuiltin((String)"is_null", var8_8, (ScriptContext)var1_1).asBool()) {
            return ScriptValue.NULL;
        }
        var9_9 = var1_1.getClassOrVar("MenuClick");
        if (!ScriptFormula.valuesEqualStr((ScriptValue)(var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "click_type", (ScriptValue)var9_9, (ScriptContext)var1_1) : ScriptValue.NULL), (String)"shift_right")) ** GOTO lbl-1000
        var10_10 = var1_1.getClassOrVar("row");
        if (var10_10 != ScriptValue.NULL) {
            var11_11 = new ArrayList<ScriptValue>();
            var11_11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "buyer_uuid"));
            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var10_10, var11_11, (ScriptContext)var1_1);
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var12_12 = var1_1.getClassOrVar("Player");
        v2 /* !! */  = var12_12 != ScriptValue.NULL ? ((var13_13 = PolyClassPlayer.ofGuarded((ScriptValue)var12_12)) != null ? var13_13.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var12_12, (ScriptContext)var1_1)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)v1 /* !! */ , (ScriptValue)v2 /* !! */ )) {
            v3 = true;
        } else lbl-1000:
        // 2 sources

        {
            v3 = false;
        }
        if (v3) {
            var14_14 = var1_1.getClassOrVar("SQL");
            if (var14_14 != ScriptValue.NULL) {
                var15_15 = new ArrayList<ScriptValue>();
                var15_15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_sponsors WHERE slot_num = ?"));
                var16_16 = new ArrayList<ScriptValue>();
                var16_16.add(var1_1.getClassOrVar("slot_num"));
                var15_15.add(ScriptFormula.callBuiltin((String)"int", var16_16, (ScriptContext)var1_1));
                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var14_14, var15_15, (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            var17_17 = var1_1.getClassOrVar("Player");
            if (var17_17 != ScriptValue.NULL) {
                var18_18 = ScriptValue.of((String)("<yellow>Removed your sponsor from slot #" + var1_1.getStr("slot_num") + "."));
                if (var17_17 instanceof ScriptValue.Obj && (var20_20 = (var19_19 = (ScriptValue.Obj)var17_17).instance()) != null && !(var20_20 instanceof PolyClass) && var19_19.typeName().equals("Player")) {
                    var21_21 = new PolyClassPlayer(var20_20);
                    v5 /* !! */  = ScriptValue.of((boolean)var21_21.tm$42_send_message(var18_18.asStr()));
                } else {
                    var22_22 = new ArrayList<ScriptValue>();
                    var22_22.add(var18_18);
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var17_17, var22_22, (ScriptContext)var1_1);
                }
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
            var23_23 = var1_1.getClassOrVar("Cmd");
            if (var23_23 != ScriptValue.NULL) {
                var24_24 = new ArrayList<ScriptValue>();
                var24_24.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "main"));
                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)var23_23, var24_24, (ScriptContext)var1_1);
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var25_25 = ScriptContext.builder().copyFrom(var1_1);
        var25_25.val("name", var7_7);
        Warps.teleportToWarp(var25_25);
        return ScriptValue.NULL;
    }

    public static ScriptValue manageTitle(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_manage_offset";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                object = polyClassPlayer2.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("offset", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        return ScriptValue.of((String)("{Images.from('cml:player_warps_submenu_1')}{Images.shift(-170)}<black> " + (scriptValue3 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassPlayer.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + "'s Warps"));
    }

    public static ScriptValue generateManageButtons(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        StringBuilder stringBuilder = new StringBuilder().append("%");
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "warps_search";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object3 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)string));
                arrayList3.add(ScriptValue.of((String)string2));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        arrayList2.add((ScriptValue)object3);
        ScriptValue scriptValue2 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"lower", arrayList2, (ScriptContext)scriptContext).asStr()).append("%").toString());
        builder.val("like", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
        if (scriptValue3 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            arrayList4.add(ScriptValue.of((String)("SELECT * FROM warps WHERE owner_uuid = ? AND (lower(name) LIKE ? OR lower(owner_name) LIKE ?) ORDER BY " + Warps.sortOrderClause(builder2).asStr())));
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            arrayList4.add((ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            arrayList4.add(scriptValue2);
            arrayList4.add(scriptValue2);
            object2 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue3, arrayList4, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("rows", scriptValue5);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue6 = Warps.playerFavouriteIds(builder3);
        builder.val("fav_ids", scriptValue6);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue7 = Warps.playerBannedIds(builder4);
        builder.val("ban_ids", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "warps_manage_offset";
            String string3 = "int";
            if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                object = polyClassPlayer.tm$12_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((String)string));
                arrayList5.add(ScriptValue.of((String)string3));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("offset", scriptValue9);
        double d = scriptValue9.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue10 = ScriptValue.of((double)d);
        builder.val("start", scriptValue10);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        arrayList6.add(scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList6, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                Object object6;
                Object object7;
                Object object8;
                Object object9;
                Object object10;
                builder.val("i", scriptValue11);
                ScriptValue scriptValue12 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue12);
                double d2 = scriptValue12.asNum();
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptValue5);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList7, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("rows");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptValue12);
                    object10 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object10 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = object10;
                builder.val("row", scriptValue14);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("row");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object9 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, arrayList9, (ScriptContext)scriptContext);
                } else {
                    object9 = ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = object9;
                builder.val("name", scriptValue16);
                ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                arrayList10.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                if (scriptValue17 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    arrayList12.add(scriptContext.getClassOrVar("i"));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList12, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                arrayList11.add((ScriptValue)object8);
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("row", scriptValue14);
                arrayList11.add(Warps.warpIconOf(builder5));
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList11.add(ScriptValue.of((String)("<yellow>" + scriptValue16.asStr())));
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("row", scriptValue14);
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("arr", scriptValue6);
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("row");
                if (scriptValue18 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                    arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList13, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                builder7.val("val", object7);
                builder6.val("is_fav", Warps.arrContains(builder7));
                ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                builder8.val("arr", scriptValue7);
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("row");
                if (scriptValue19 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, arrayList14, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                builder8.val("val", object6);
                builder6.val("is_ban", Warps.arrContains(builder8));
                arrayList11.add(Warps.warpLoreOf(builder6));
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList11.add(ScriptValue.of((String)("warps.pf:on_mywarps_click:" + scriptValue16.asStr())));
                arrayList10.add(ScriptFormula.callBuiltin((String)"make_map", arrayList11, (ScriptContext)scriptContext));
                ScriptValue scriptValue20 = ScriptFormula.callBuiltin((String)"push", arrayList10, (ScriptContext)scriptContext);
                builder.val("out", scriptValue20);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue onMywarpsClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_current_warp";
            String string2 = "string";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("name");
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("MenuClick");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "click_type", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"right")) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "warps_return_page";
                String string3 = "string";
                ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage");
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visitedit"));
                v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
            if (scriptValue7 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "managewarp"));
                v3 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue maxManageOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.ownedWarpNames(builder2));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
    }

    public static ScriptValue prevManagePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_manage_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_manage_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptValue.of((double)(scriptValue3.asNum() - 1.0));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue nextManagePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_manage_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        if (scriptValue3.asNum() < Warps.maxManageOffset(builder2).asNum()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_manage_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue favouriteTitle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "{Images.from('cml:player_warps_submenu_1')}{Images.shift(-170)}<black> Favourite Warps");
    }

    public static ScriptValue favouriteRows(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        StringBuilder stringBuilder = new StringBuilder().append("%");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_search";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object2);
        ScriptValue scriptValue2 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"lower", arrayList, (ScriptContext)scriptContext).asStr()).append("%").toString());
        builder.val("like", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
        if (scriptValue3 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(ScriptValue.of((String)("SELECT w.* FROM warp_favourites f JOIN warps w ON w.id = f.warp_id " + "WHERE f.player_uuid = ? AND (lower(w.name) LIKE ? OR lower(w.owner_name) LIKE ?) ORDER BY w.name ASC")));
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            arrayList3.add((ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            arrayList3.add(scriptValue2);
            arrayList3.add(scriptValue2);
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue generateFavouriteButtons(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.favouriteRows(builder2);
        builder.val("rows", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = Warps.playerBannedIds(builder3);
        builder.val("ban_ids", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_favourite_offset";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("offset", scriptValue4);
        double d = scriptValue4.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("start", scriptValue5);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        arrayList3.add(scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                Object object3;
                Object object4;
                Object object5;
                Object object6;
                builder.val("i", scriptValue6);
                ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue7);
                double d2 = scriptValue7.asNum();
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("rows");
                if (scriptValue8 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue7);
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue9 = object6;
                builder.val("row", scriptValue9);
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("row");
                if (scriptValue10 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue11 = object5;
                builder.val("name", scriptValue11);
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(scriptContext.getClassOrVar("i"));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList9, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                arrayList8.add((ScriptValue)object4);
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("row", scriptValue9);
                arrayList8.add(Warps.warpIconOf(builder4));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList8.add(ScriptValue.of((String)("<yellow>" + scriptValue11.asStr())));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("row", scriptValue9);
                builder5.val("is_fav",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 1));
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("arr", scriptValue2);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("row");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList10, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                builder6.val("val", object3);
                builder5.val("is_ban", Warps.arrContains(builder6));
                arrayList8.add(Warps.warpLoreOf(builder5));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList8.add(ScriptValue.of((String)("warps.pf:on_favourite_click:" + scriptValue11.asStr())));
                arrayList7.add(ScriptFormula.callBuiltin((String)"make_map", arrayList8, (ScriptContext)scriptContext));
                ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"push", arrayList7, (ScriptContext)scriptContext);
                builder.val("out", scriptValue14);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue onFavouriteClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("MenuClick");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "click_type", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"right")) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "warps_current_warp";
                String string2 = "string";
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("name");
                if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue3));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    arrayList.add(scriptValue3);
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "warps_return_page";
                String string3 = "string";
                ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "favourite");
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visitedit"));
                v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", scriptContext.getClassOrVar("name"));
            Warps.teleportToWarp(builder2);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue maxFavouriteOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.favouriteRows(builder2));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
    }

    public static ScriptValue prevFavouritePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_favourite_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_favourite_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptValue.of((double)(scriptValue3.asNum() - 1.0));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "favourite"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue nextFavouritePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_favourite_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        if (scriptValue3.asNum() < Warps.maxFavouriteOffset(builder2).asNum()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_favourite_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "favourite"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue toggleFavourite(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptContext.getClassOrVar("name"));
        ScriptValue scriptValue = Warps.warpId(builder2);
        builder.val("wid", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptContext.getClassOrVar("name"));
        ScriptValue scriptValue2 = Warps.isFavourite(builder3);
        builder.val("was_fav", scriptValue2);
        if (scriptValue2.asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
            if (scriptValue3 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_favourites WHERE warp_id = ? AND player_uuid = ?"));
                arrayList.add(scriptValue);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
                arrayList.add((ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                v0 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue6 = ScriptValue.of((String)("<yellow>Removed <white>" + scriptContext.getStr("name") + " <yellow>from favourites."));
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue6.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue6);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("SQL");
            if (scriptValue7 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_favourites (warp_id, player_uuid) VALUES (?, ?)"));
                arrayList.add(scriptValue);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
                arrayList.add((ScriptValue)(scriptValue8 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue10 = ScriptValue.of((String)("<green>Added <white>" + scriptContext.getStr("name") + " <green>to favourites."));
                if (scriptValue9 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue10.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue10);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue currentWarp(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_current_warp";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue managewarpTitle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "locked"));
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        arrayList.add(PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder3), arrayList2, (ScriptContext)scriptContext));
        boolean bl = ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
        ScriptValue scriptValue2 = ScriptValue.of((boolean)bl);
        builder.val("locked", scriptValue2);
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "cml:pwarp_setting_enabled");
        builder.val("state_img", scriptValue3);
        if (bl) {
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "cml:pwarp_setting_disabled");
            builder.val("state_img", scriptValue4);
        }
        return ScriptValue.of((String)("{Images.from('cml:player_warps_manage')}{Images.from('" + scriptContext.getStr("state_img") + "', -129)}{Images.shift(-71)}<black> Manage > " + scriptValue.asStr()));
    }

    public static ScriptValue mwTeleport(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", Warps.currentWarp(builder3));
        Warps.teleportToWarp(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenDescDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Set"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_desc_submit"));
        ArrayList<Object> arrayList3 = new ArrayList<Object>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Description"));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "desc"));
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        arrayList3.add(PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder3), arrayList4, (ScriptContext)scriptContext));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Warp Description"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onDescSubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET desc = ? WHERE id = ?"));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("s", scriptContext.getClassOrVar("value"));
            arrayList.add(Warps.sanitize(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue);
            arrayList.add(Warps.warpId(builder5));
            v0 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Description updated.";
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwToggleLock(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue);
        ScriptValue scriptValue2 = Warps.warpRow(builder4);
        builder.val("data", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("data");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "locked"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        if (ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)))) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("SQL");
            if (scriptValue4 != ScriptValue.NULL) {
                Object object2;
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 0 WHERE id = ?"));
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("data");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object2);
                v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7 = ScriptValue.of((String)("<green>\u2714 <white>" + scriptValue.asStr() + " is now public."));
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue7.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue7);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList5, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
            if (scriptValue8 != ScriptValue.NULL) {
                Object object4;
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 1 WHERE id = ?"));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("data");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                arrayList6.add((ScriptValue)object4);
                v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, arrayList6, (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ScriptValue scriptValue11 = ScriptValue.of((String)("<yellow>" + scriptValue.asStr() + " is now locked (hidden from the browser)."));
                if (scriptValue10 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                    v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptValue11);
                    v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList8, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue12 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
            arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "managewarp"));
            v7 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue12, arrayList9, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenCategoryDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("MenuClick");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "click_type", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"right")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
            if (scriptValue3 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = 'other' WHERE id = ?"));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue);
                arrayList.add(Warps.warpId(builder3));
                v0 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<yellow>Category removed (reset to 'other').";
                if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Set"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_category_submit"));
        ArrayList<Object> arrayList3 = new ArrayList<Object>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Category"));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "category"));
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue);
        arrayList3.add(PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder4), arrayList4, (ScriptContext)scriptContext));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Warp Category"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onCategorySubmit(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = Warps.currentWarp(builder2);
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("s", scriptContext.getClassOrVar("value"));
        ScriptValue scriptValue3 = Warps.sanitize(builder4);
        builder.val("cat", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "other");
            builder.val("cat", scriptValue4);
        }
        if ((scriptValue = scriptContext.getClassOrVar("SQL")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = ? WHERE id = ?"));
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("cat"));
            arrayList.add(ScriptFormula.callBuiltin((String)"lower", arrayList2, (ScriptContext)scriptContext));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder5));
            v0 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue6 = ScriptValue.of((String)("<green>\u2714 <white>Category set to '" + scriptContext.getStr("cat") + "'."));
            if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue6.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue6);
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenBanDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Ban"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_ban_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Player Name"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Ban Player"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onBanSubmit(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("s", scriptContext.getClassOrVar("value"));
        ScriptValue scriptValue2 = Warps.sanitize(builder4);
        builder.val("target_name", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue4 = scriptValue2;
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$10_get_player(scriptValue4.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("target", scriptValue5);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue5);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue2.asStr() + " is not online."));
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue7.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptValue7);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        builder5.val("name", scriptValue);
        ScriptValue scriptValue8 = Warps.warpId(builder5);
        builder.val("wid", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?"));
            arrayList3.add(scriptValue8);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("target");
            arrayList3.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
            v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, arrayList3, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("SQL");
        if (scriptValue11 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_bans (warp_id, uuid, name) VALUES (?, ?, ?)"));
            arrayList4.add(scriptValue8);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("target");
            arrayList4.add((ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("target");
            arrayList4.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL));
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue11, arrayList4, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue15;
            ScriptValue scriptValue16 = ScriptValue.of((String)("<green>\u2714 <white>Banned " + ((scriptValue15 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " from " + scriptValue.asStr() + "."));
            if (scriptValue14 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue16.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue16);
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwSetIcon(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("held", scriptValue3);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue3);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>Hold an item first.";
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("SQL");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET icon = ? WHERE id = ?"));
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("held");
            arrayList3.add((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue);
            arrayList3.add(Warps.warpId(builder4));
            v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Icon updated.";
            if (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object);
                v2 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenVisited(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_visited_offset";
            String string2 = "int";
            ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenBanned(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_banned_offset";
            String string2 = "int";
            ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenRenameDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rename"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_rename_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "New Name"));
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList3.add(Warps.currentWarp(builder2));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rename Warp"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue onRenameSubmit(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = ScriptContext.builder().copyFrom(var1_1);
        var3_3 = Warps.currentWarp(var2_2);
        var0.val("old_name", var3_3);
        var4_4 = ScriptContext.builder().copyFrom(var1_1);
        var4_4.val("s", var1_1.getClassOrVar("value"));
        var5_5 = Warps.sanitize(var4_4);
        var0.val("new_name", var5_5);
        var6_6 = ScriptContext.builder().copyFrom(var1_1);
        var6_6.val("name", var3_3);
        if ((Warps.isOwner(var6_6).asBool() ^ true) != false || ScriptFormula.valuesEqualStr((ScriptValue)var5_5, (String)"") != false) ** GOTO lbl-1000
        var7_7 = ScriptContext.builder().copyFrom(var1_1);
        var7_7.val("name", var5_5);
        if (!Warps.warpExists(var7_7).asBool()) {
            v0 = false;
        } else lbl-1000:
        // 2 sources

        {
            v0 = true;
        }
        if (v0) {
            var8_8 = var1_1.getClassOrVar("Player");
            if (var8_8 != ScriptValue.NULL) {
                var9_9 = "<red>\u2718 <white>Could not rename that warp.";
                if (var8_8 instanceof ScriptValue.Obj && (var11_11 = (var10_10 = (ScriptValue.Obj)var8_8).instance()) != null && !(var11_11 instanceof PolyClass) && var10_10.typeName().equals("Player")) {
                    var12_12 = new PolyClassPlayer(var11_11);
                    v1 /* !! */  = ScriptValue.of((boolean)var12_12.tm$42_send_message(var9_9));
                } else {
                    var13_13 = new ArrayList<ScriptValue>();
                    var13_13.add(ScriptValue.of((String)var9_9));
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var8_8, var13_13, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var14_14 = var1_1.getClassOrVar("SQL");
        if (var14_14 != ScriptValue.NULL) {
            var15_15 = new ArrayList<ScriptValue>();
            var15_15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET name = ? WHERE id = ?"));
            var15_15.add(var5_5);
            var16_16 = ScriptContext.builder().copyFrom(var1_1);
            var16_16.val("name", var3_3);
            var15_15.add(Warps.warpId(var16_16));
            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var14_14, var15_15, (ScriptContext)var1_1);
        } else {
            v2 /* !! */  = ScriptValue.NULL;
        }
        var17_17 = var1_1.getClassOrVar("Player");
        if (var17_17 != ScriptValue.NULL) {
            var18_18 = "warps_current_warp";
            var19_19 = "string";
            var20_20 = var5_5;
            if (var17_17 instanceof ScriptValue.Obj && (var22_22 = (var21_21 = (ScriptValue.Obj)var17_17).instance()) != null && !(var22_22 instanceof PolyClass) && var21_21.typeName().equals("Player")) {
                var23_23 = new PolyClassPlayer(var22_22);
                v3 /* !! */  = ScriptValue.of((boolean)var23_23.tm$30_set_typed(var18_18, var19_19, var20_20));
            } else {
                var24_24 = new ArrayList<ScriptValue>();
                var24_24.add(ScriptValue.of((String)var18_18));
                var24_24.add(ScriptValue.of((String)var19_19));
                var24_24.add(var20_20);
                v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var17_17, var24_24, (ScriptContext)var1_1);
            }
        } else {
            v3 /* !! */  = ScriptValue.NULL;
        }
        var25_25 = var1_1.getClassOrVar("Player");
        if (var25_25 != ScriptValue.NULL) {
            var26_26 = ScriptValue.of((String)("<green>\u2714 <white>Renamed to '" + var5_5.asStr() + "'."));
            if (var25_25 instanceof ScriptValue.Obj && (var28_28 = (var27_27 = (ScriptValue.Obj)var25_25).instance()) != null && !(var28_28 instanceof PolyClass) && var27_27.typeName().equals("Player")) {
                var29_29 = new PolyClassPlayer(var28_28);
                v4 /* !! */  = ScriptValue.of((boolean)var29_29.tm$42_send_message(var26_26.asStr()));
            } else {
                var30_30 = new ArrayList<ScriptValue>();
                var30_30.add(var26_26);
                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var25_25, var30_30, (ScriptContext)var1_1);
            }
        } else {
            v4 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwDelete(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.deleteWarp(builder3).asBool()) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue3 = ScriptValue.of((String)("<green>\u2714 <white>Deleted warp '" + scriptValue.asStr() + "'."));
                if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue3.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue3);
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage"));
                v1 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenTransferDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Transfer"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_transfer_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "New Owner Name"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Transfer Ownership"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onTransferSubmit(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("s", scriptContext.getClassOrVar("value"));
        ScriptValue scriptValue2 = Warps.sanitize(builder4);
        builder.val("target_name", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue4 = scriptValue2;
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$10_get_player(scriptValue4.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("target", scriptValue5);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue5);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue2.asStr() + " is not online."));
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue7.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptValue7);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET owner_uuid = ?, owner_name = ? WHERE id = ?"));
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("target");
            arrayList3.add((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("target");
            arrayList3.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue);
            arrayList3.add(Warps.warpId(builder5));
            v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, arrayList3, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue12;
            ScriptValue scriptValue13 = ScriptValue.of((String)("<green>\u2714 <white>Transferred " + scriptValue.asStr() + " to " + ((scriptValue12 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "."));
            if (scriptValue11 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue13.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue13);
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue14 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage"));
            v4 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue14, arrayList5, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue bannedTitle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("{Images.from('cml:player_warps_banned_players')}{Images.shift(-170)}<black> Banned Players > " + Warps.currentWarp(builder2).asStr()));
    }

    public static ScriptValue bannedRecords(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_banned_search";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"lower", arrayList, (ScriptContext)scriptContext);
        builder.val("search", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", Warps.currentWarp(builder3));
        ScriptValue scriptValue3 = Warps.warpId(builder2);
        builder.val("wid", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            Object object3;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("SQL");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name FROM warp_bans WHERE warp_id = ? ORDER BY name"));
                arrayList3.add(scriptValue3);
                object3 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = object3;
            builder.val("rows", scriptValue5);
        } else {
            Object object4;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name FROM warp_bans WHERE warp_id = ? AND lower(name) LIKE ? ORDER BY name"));
                arrayList4.add(scriptValue3);
                arrayList4.add(ScriptValue.of((String)("%" + scriptValue2.asStr() + "%")));
                object4 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue6, arrayList4, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = object4;
            builder.val("rows", scriptValue7);
        }
        ArrayList arrayList5 = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList5);
        builder.val("out", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("rows"));
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                Object object5;
                Object object6;
                builder.val("row", scriptValue8);
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                arrayList7.add((ScriptValue)object6);
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("row");
                if (scriptValue10 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, arrayList9, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                arrayList7.add((ScriptValue)object5);
                arrayList6.add(ScriptFormula.callBuiltin((String)"make_map", arrayList7, (ScriptContext)scriptContext));
                ScriptValue scriptValue11 = ScriptFormula.callBuiltin((String)"push", arrayList6, (ScriptContext)scriptContext);
                builder.val("out", scriptValue11);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue openBannedSearchDialog(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_banned_search_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Player name"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_banned_search";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                arrayList4.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList3.add((ScriptValue)object2);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search Banned Players"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onBannedSearchSubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_banned_search";
            String string2 = "string";
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("s", scriptContext.getClassOrVar("value"));
            ScriptValue scriptValue2 = Warps.sanitize(builder2);
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_banned_offset";
            String string3 = "int";
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue4));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue4);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue generateBannedButtons(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.bannedRecords(builder2);
        builder.val("records", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_banned_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        double d = scriptValue3.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("start", scriptValue4);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        arrayList3.add(scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                Object object3;
                Object object4;
                Object object5;
                Object object6;
                builder.val("i", scriptValue5);
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue6);
                double d2 = scriptValue6.asNum();
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("records");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue6);
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = object6;
                builder.val("entry", scriptValue8);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("entry");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object5;
                builder.val("target_uuid", scriptValue10);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entry");
                if (scriptValue11 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = object4;
                builder.val("target_name", scriptValue12);
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add(scriptContext.getClassOrVar("out"));
                ArrayList<Object> arrayList9 = new ArrayList<Object>();
                arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(scriptContext.getClassOrVar("i"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList10, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList9.add(object3);
                arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue12);
                arrayList9.add(Warps.playerHeadIcon(builder3));
                arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList9.add(ScriptValue.of((String)("<white>" + scriptValue12.asStr())));
                arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                arrayList11.add(ScriptValue.of((String)("<gray>Banned from <white>" + Warps.currentWarp(builder4).asStr())));
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>Left-click to unban"));
                arrayList9.add(new ScriptValue.Array(arrayList11));
                arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList9.add(ScriptValue.of((String)("warps.pf:on_unban_click:" + scriptValue10.asStr())));
                arrayList8.add(ScriptFormula.callBuiltin((String)"make_map", arrayList9, (ScriptContext)scriptContext));
                ScriptValue scriptValue14 = ScriptFormula.callBuiltin((String)"push", arrayList8, (ScriptContext)scriptContext);
                builder.val("out", scriptValue14);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue onUnbanClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?"));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue);
            arrayList.add(Warps.warpId(builder4));
            arrayList.add(scriptContext.getClassOrVar("target_uuid"));
            v0 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Unbanned.";
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue maxBannedOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.bannedRecords(builder2));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
    }

    public static ScriptValue prevBannedPage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_banned_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_banned_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptValue.of((double)(scriptValue3.asNum() - 1.0));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue nextBannedPage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_banned_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        if (scriptValue3.asNum() < Warps.maxBannedOffset(builder2).asNum()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_banned_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue visitedTitle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("{Images.from('cml:player_warps_visited_players')}{Images.shift(-170)}<black> Visited Players > " + Warps.currentWarp(builder2).asStr()));
    }

    public static ScriptValue visitorRecords(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_visited_search";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"lower", arrayList, (ScriptContext)scriptContext);
        builder.val("search", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", Warps.currentWarp(builder3));
        ScriptValue scriptValue3 = Warps.warpId(builder2);
        builder.val("wid", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            Object object3;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("SQL");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name, count FROM warp_visitors WHERE warp_id = ? ORDER BY name"));
                arrayList3.add(scriptValue3);
                object3 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = object3;
            builder.val("rows", scriptValue5);
        } else {
            Object object4;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name, count FROM warp_visitors WHERE warp_id = ? AND lower(name) LIKE ? ORDER BY name"));
                arrayList4.add(scriptValue3);
                arrayList4.add(ScriptValue.of((String)("%" + scriptValue2.asStr() + "%")));
                object4 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue6, arrayList4, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = object4;
            builder.val("rows", scriptValue7);
        }
        ArrayList arrayList5 = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList5);
        builder.val("out", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("rows"));
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                Object object5;
                Object object6;
                Object object7;
                builder.val("row", scriptValue8);
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                arrayList7.add((ScriptValue)object7);
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("row");
                if (scriptValue10 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, arrayList9, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                arrayList7.add((ScriptValue)object6);
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("row");
                if (scriptValue11 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList10, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                arrayList7.add((ScriptValue)object5);
                arrayList6.add(ScriptFormula.callBuiltin((String)"make_map", arrayList7, (ScriptContext)scriptContext));
                ScriptValue scriptValue12 = ScriptFormula.callBuiltin((String)"push", arrayList6, (ScriptContext)scriptContext);
                builder.val("out", scriptValue12);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue openVisitedSearchDialog(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_visited_search_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Player name"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_visited_search";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                arrayList4.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList3.add((ScriptValue)object2);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search Visited Players"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onVisitedSearchSubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_visited_search";
            String string2 = "string";
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("s", scriptContext.getClassOrVar("value"));
            ScriptValue scriptValue2 = Warps.sanitize(builder2);
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "warps_visited_offset";
            String string3 = "int";
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue4));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue4);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue generateVisitedButtons(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.visitorRecords(builder2);
        builder.val("records", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_visited_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        double d = scriptValue3.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("start", scriptValue4);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        arrayList3.add(scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                Object object3;
                Object object4;
                Object object5;
                Object object6;
                builder.val("i", scriptValue5);
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue6);
                double d2 = scriptValue6.asNum();
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("records");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue6);
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = object6;
                builder.val("entry", scriptValue8);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("entry");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object5;
                builder.val("visitor_name", scriptValue10);
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptContext.getClassOrVar("out"));
                ArrayList<Object> arrayList8 = new ArrayList<Object>();
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                if (scriptValue11 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(scriptContext.getClassOrVar("i"));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList9, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                arrayList8.add(object4);
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue10);
                arrayList8.add(Warps.playerHeadIcon(builder3));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList8.add(ScriptValue.of((String)("<white>" + scriptValue10.asStr())));
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                StringBuilder stringBuilder = new StringBuilder().append("<gray>Visited <white>");
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("entry");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    arrayList12.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList12, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList11.add((ScriptValue)object3);
                arrayList10.add(ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"int", arrayList11, (ScriptContext)scriptContext).asStr()).append(" <gray>time(s)").toString()));
                arrayList8.add(new ScriptValue.Array(arrayList10));
                arrayList7.add(ScriptFormula.callBuiltin((String)"make_map", arrayList8, (ScriptContext)scriptContext));
                ScriptValue scriptValue13 = ScriptFormula.callBuiltin((String)"push", arrayList7, (ScriptContext)scriptContext);
                builder.val("out", scriptValue13);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue maxVisitedOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.visitorRecords(builder2));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
    }

    public static ScriptValue prevVisitedPage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_visited_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_visited_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptValue.of((double)(scriptValue3.asNum() - 1.0));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue nextVisitedPage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_visited_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        if (scriptValue3.asNum() < Warps.maxVisitedOffset(builder2).asNum()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_visited_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue visiteditTitle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("{Images.from('cml:player_warps_manage_visit')}{Images.shift(-170)}<black> Manage Visit > " + Warps.currentWarp(builder2).asStr()));
    }

    public static ScriptValue onVisiteditBack(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_return_page";
            String string2 = "string";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("target", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps");
            builder.val("target", scriptValue4);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("target"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue viTeleport(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", Warps.currentWarp(builder3));
        Warps.teleportToWarp(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue viToggleFavourite(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", Warps.currentWarp(builder3));
        Warps.toggleFavourite(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue viOpenRateDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Cmd"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rate"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_rate_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Stars (0-5)"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 5.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 5.0));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rate Warp"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_number", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onRateSubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        if (Warps.isOwner(builder3).asBool()) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You can't rate your own warp.";
                if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("stars"));
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue3);
        if (scriptValue3.asNum() < 0.0) {
            double d = 0.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)0.0);
            builder.val("n", scriptValue4);
        }
        if (scriptContext.getNum("n") > 5.0) {
            double d = 5.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)5.0);
            builder.val("n", scriptValue5);
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue);
        ScriptValue scriptValue6 = Warps.warpId(builder4);
        builder.val("wid", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("SQL");
        if (scriptValue7 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_ratings WHERE warp_id = ? AND uuid = ?"));
            arrayList2.add(scriptValue6);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            arrayList2.add((ScriptValue)(scriptValue8 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue7, arrayList2, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        if (scriptValue9 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            PolyClassPlayer polyClassPlayer2;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_ratings (warp_id, uuid, name, stars) VALUES (?, ?, ?, ?)"));
            arrayList3.add(scriptValue6);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            arrayList3.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassPlayer2.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
            arrayList3.add((ScriptValue)(scriptValue11 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            arrayList3.add(scriptContext.getClassOrVar("n"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, arrayList3, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue13 = ScriptValue.of((String)("<green>\u2714 <white>Rated " + scriptValue.asStr() + " " + scriptContext.getStr("n") + " star(s)."));
            if (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue13.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue13);
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue rateTitle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("{Images.from('cml:player_warps_submenu_2')}{Images.shift(-170)}<black> Warp Rates > " + Warps.currentWarp(builder2).asStr()));
    }

    public static ScriptValue ratingRecords(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name, stars FROM warp_ratings WHERE warp_id = ? ORDER BY name"));
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", Warps.currentWarp(builder3));
            arrayList.add(Warps.warpId(builder2));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("rows", scriptValue2);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue2);
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                Object object2;
                Object object3;
                Object object4;
                builder.val("row", scriptValue3);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object4);
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("row");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object3);
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars"));
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("row");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object2);
                arrayList2.add(ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext));
                ScriptValue scriptValue7 = ScriptFormula.callBuiltin((String)"push", arrayList2, (ScriptContext)scriptContext);
                builder.val("out", scriptValue7);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue generateRateButtons(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.ratingRecords(builder2);
        builder.val("records", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_rate_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        double d = scriptValue3.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("start", scriptValue4);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
        arrayList3.add(scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                Object object3;
                PolyClassPlayer polyClassPlayer;
                Object object4;
                Object object5;
                Object object6;
                Object object7;
                builder.val("i", scriptValue5);
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptContext.getClassOrVar("i"));
                builder.val("global_idx", scriptValue6);
                double d2 = scriptValue6.asNum();
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue);
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("records");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue6);
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = object7;
                builder.val("entry", scriptValue8);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("entry");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object6;
                builder.val("rater_uuid", scriptValue10);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entry");
                if (scriptValue11 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = object5;
                builder.val("rater_name", scriptValue12);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("entry");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars"));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = object4;
                builder.val("stars", scriptValue14);
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(ScriptValue.of((String)("<gray>Rating: <yellow>" + scriptValue14.asStr() + "/5")));
                ScriptValue.Array array2 = new ScriptValue.Array(arrayList9);
                builder.val("lore", (ScriptValue)array2);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                Object object8 = scriptValue15 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)object8)) {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(scriptContext.getClassOrVar("lore"));
                    arrayList10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                    ScriptValue scriptValue16 = ScriptFormula.callBuiltin((String)"push", arrayList10, (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue16);
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    arrayList11.add(scriptContext.getClassOrVar("lore"));
                    arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>Left-click to remove your rating"));
                    ScriptValue scriptValue17 = ScriptFormula.callBuiltin((String)"push", arrayList11, (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue17);
                }
                ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                arrayList12.add(scriptContext.getClassOrVar("out"));
                ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                if (scriptValue18 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add(scriptContext.getClassOrVar("i"));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, arrayList14, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                arrayList13.add((ScriptValue)object3);
                arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue12);
                arrayList13.add(Warps.playerHeadIcon(builder3));
                arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList13.add(ScriptValue.of((String)("<white>" + scriptValue12.asStr() + "'s rate")));
                arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                arrayList13.add(scriptContext.getClassOrVar("lore"));
                arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList13.add(ScriptValue.of((String)("warps.pf:on_unrate_click:" + scriptValue10.asStr())));
                arrayList12.add(ScriptFormula.callBuiltin((String)"make_map", arrayList13, (ScriptContext)scriptContext));
                ScriptValue scriptValue19 = ScriptFormula.callBuiltin((String)"push", arrayList12, (ScriptContext)scriptContext);
                builder.val("out", scriptValue19);
            }
        }
        return scriptContext.getClassOrVar("out");
    }

    public static ScriptValue onUnrateClick(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("rater_uuid");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        Object object = scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)object) ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_ratings WHERE warp_id = ? AND uuid = ?"));
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", Warps.currentWarp(builder3));
            arrayList.add(Warps.warpId(builder2));
            arrayList.add(scriptContext.getClassOrVar("rater_uuid"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "rate"));
            v3 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue maxRateOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        arrayList.add(Warps.ratingRecords(builder2));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        arrayList2.add(ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)));
        return ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
    }

    public static ScriptValue prevRatePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_rate_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_rate_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptValue.of((double)(scriptValue3.asNum() - 1.0));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "rate"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue nextRatePage(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_rate_offset";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        if (scriptValue3.asNum() < Warps.maxRateOffset(builder2).asNum()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "warps_rate_offset";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "rate"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdHelp(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<yellow>--- /warps commands ---";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps <gray>- open the warps menu";
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps create|set <name> <gray>- create a warp (set also repositions your own)";
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps remove <warp> <gray>- delete a warp you own";
            if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps desc set <warp> <text> <gray>|<white> desc remove <warp>";
            if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps list <gray>|<white> listof <player>";
            if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps amount <gray>|<white> amountof <player>";
            if (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps icon set <warp> <gray>(uses held item) <white>|<gray> icon remove <warp>";
            if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v7 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps category set <warp> <category> <gray>|<white> category remove <warp>";
            if (scriptValue9 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v8 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps rate <warp> <0-5>";
            if (scriptValue10 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps lock <warp> <gray>- toggle public/hidden";
            if (scriptValue11 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v10 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps reset <warp> <gray>- move it to your location";
            if (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v11 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v11 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps rename <warp> <new name>";
            if (scriptValue13 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v12 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps setowner <warp> <player>";
            if (scriptValue14 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v13 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v13 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v13 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps ban set <warp> <player> <gray>|<white> ban remove <warp> <player>";
            if (scriptValue15 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v14 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v14 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v14 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps favourite <warp> <gray>- toggle favourite";
            if (scriptValue16 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v15 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v15 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v15 = ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Player");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<white>/warps buyslot <gray>- spend XP for an extra warp slot";
            if (scriptValue17 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v16 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v16 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v16 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdRemove(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE id = ?"));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder4));
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue8 = ScriptValue.of((String)("<green>\u2714 <white>Deleted warp '" + scriptValue2.asStr() + "'."));
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue8.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue8);
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdDescSet(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            Object object4;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET desc = ? WHERE id = ?"));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
            if (scriptValue7 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "text"));
                object4 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue7, arrayList2, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            builder4.val("s", object4);
            arrayList.add(Warps.sanitize(builder4));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder5));
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "<green>\u2714 <white>Description updated.";
            if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdDescRemove(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET desc = '' WHERE id = ?"));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder4));
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "<green>\u2714 <white>Description cleared.";
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue warpNamesSummary(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT name FROM warps WHERE owner_uuid = ? ORDER BY created_at ASC"));
            arrayList.add(scriptContext.getClassOrVar("owner_uuid"));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("rows", scriptValue2);
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "");
        builder.val("names", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue2);
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                Object object2;
                builder.val("row", scriptValue4);
                if (scriptContext.getStr("names").equals("")) {
                    Object object3;
                    ScriptValue scriptValue5 = scriptContext.getClassOrVar("row");
                    if (scriptValue5 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                        object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                    } else {
                        object3 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue6 = object3;
                    builder.val("names", scriptValue6);
                    continue;
                }
                StringBuilder stringBuilder = new StringBuilder().append(scriptContext.getStr("names")).append("<gray>, <white>");
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = ScriptValue.of((String)stringBuilder.append(object2.asStr()).toString());
                builder.val("names", scriptValue8);
            }
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue2);
        arrayList.add(ScriptFormula.callBuiltin((String)"len", arrayList2, (ScriptContext)scriptContext));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "names"));
        arrayList.add(scriptContext.getClassOrVar("names"));
        return ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue cmdList(ScriptContext.Builder builder) {
        Object object;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        builder2.val("owner_uuid", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue2 = Warps.warpNamesSummary(builder2);
        builder.val("info", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("info");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        if (ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<yellow>You have no warps.";
                if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            Object object4;
            Object object5;
            StringBuilder stringBuilder = new StringBuilder().append("<yellow>Your warps (");
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("info");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
                object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList4, (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            StringBuilder stringBuilder2 = stringBuilder.append(object5.asStr()).append("): <white>");
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("info");
            if (scriptValue7 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "names"));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = ScriptValue.of((String)stringBuilder2.append(object4.asStr()).toString());
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object3);
                v6 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(scriptValue8.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptValue8);
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList6, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdListof(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("target", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("target");
        builder2.val("owner_uuid", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue4 = Warps.warpNamesSummary(builder2);
        builder.val("info", scriptValue4);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("info");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        if (ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7;
                ScriptValue scriptValue8 = ScriptValue.of((String)("<yellow>" + ((scriptValue7 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " has no warps."));
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue8.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue8);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            Object object5;
            Object object6;
            ScriptValue scriptValue10;
            StringBuilder stringBuilder = new StringBuilder().append("<yellow>").append(((scriptValue10 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).append("'s warps (");
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("info");
            if (scriptValue11 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
                object6 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, arrayList4, (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            StringBuilder stringBuilder2 = stringBuilder.append(object6.asStr()).append("): <white>");
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("info");
            if (scriptValue12 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "names"));
                object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList5, (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = ScriptValue.of((String)stringBuilder2.append(object5.asStr()).toString());
            if (scriptValue9 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v7 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue13.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptValue13);
                v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, arrayList6, (ScriptContext)scriptContext);
            }
        } else {
            v7 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdAmount(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            StringBuilder stringBuilder = new StringBuilder().append("<yellow>You have <white>");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            arrayList.add(Warps.ownedWarpNames(builder2));
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue2 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asStr()).append("/").append(Warps.effectiveLimit(builder3).asStr()).append("<yellow> warps.").toString());
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue2);
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdAmountof(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("target", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE owner_uuid = ?"));
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("target");
            arrayList2.add((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue7;
            ScriptValue scriptValue8 = ScriptValue.of((String)("<yellow>" + ((scriptValue7 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " has <white>" + scriptValue5.asStr() + "<yellow> warp(s)."));
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue8.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptValue8);
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdIconSet(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassPlayer.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("held", scriptValue7);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue7);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>Hold an item first.";
                if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object4);
                    v3 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET icon = ? WHERE id = ?"));
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("held");
            arrayList3.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            arrayList3.add(Warps.warpId(builder4));
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, arrayList3, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "<green>\u2714 <white>Icon updated.";
            if (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object5);
                v5 = ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdIconRemove(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET icon = '' WHERE id = ?"));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder4));
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "<green>\u2714 <white>Icon reset to default.";
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdCategorySet(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("name", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue3);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue5 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue3.asStr() + "'."));
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue5.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue5);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue3);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "category"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        builder4.val("s", object);
        ScriptValue scriptValue8 = Warps.sanitize(builder4);
        builder.val("cat", scriptValue8);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue8, (String)"")) {
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "other");
            builder.val("cat", scriptValue9);
        }
        if ((scriptValue = scriptContext.getClassOrVar("SQL")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = ? WHERE id = ?"));
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("cat"));
            arrayList.add(ScriptFormula.callBuiltin((String)"lower", arrayList2, (ScriptContext)scriptContext));
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue3);
            arrayList.add(Warps.warpId(builder5));
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>Category set to '" + scriptContext.getStr("cat") + "'."));
            if (scriptValue10 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue11);
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdCategoryRemove(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = 'other' WHERE id = ?"));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder4));
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "<yellow>Category reset to 'other'.";
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdRate(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.isOwner(builder3).asBool()) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>You can't rate your own warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue6, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        ScriptValue scriptValue7 = ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue7);
        if (scriptValue7.asNum() < 0.0) {
            double d = 0.0;
            ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
            builder.val("n", scriptValue8);
        }
        if (scriptContext.getNum("n") > 5.0) {
            double d = 5.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)5.0);
            builder.val("n", scriptValue9);
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue2);
        ScriptValue scriptValue10 = Warps.warpId(builder4);
        builder.val("wid", scriptValue10);
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("SQL");
        if (scriptValue11 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_ratings WHERE warp_id = ? AND uuid = ?"));
            arrayList3.add(scriptValue10);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
            arrayList3.add((ScriptValue)(scriptValue12 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassPlayer.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue11, arrayList3, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("SQL");
        if (scriptValue13 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            PolyClassPlayer polyClassPlayer2;
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_ratings (warp_id, uuid, name, stars) VALUES (?, ?, ?, ?)"));
            arrayList4.add(scriptValue10);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            arrayList4.add((ScriptValue)(scriptValue14 != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassPlayer2.pg$34_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
            arrayList4.add((ScriptValue)(scriptValue15 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            arrayList4.add(scriptContext.getClassOrVar("n"));
            v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue13, arrayList4, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue17 = ScriptValue.of((String)("<green>\u2714 <white>Rated " + scriptValue2.asStr() + " " + scriptContext.getStr("n") + " star(s)."));
            if (scriptValue16 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue17.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue17);
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdLock(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue2);
        ScriptValue scriptValue6 = Warps.warpRow(builder4);
        builder.val("row", scriptValue6);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "locked"));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        if (ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)))) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
            if (scriptValue8 != ScriptValue.NULL) {
                Object object5;
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 0 WHERE id = ?"));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList4, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                arrayList3.add((ScriptValue)object5);
                v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, arrayList3, (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>" + scriptValue2.asStr() + " is now public."));
                if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                    v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue11);
                    v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList5, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("SQL");
            if (scriptValue12 != ScriptValue.NULL) {
                Object object7;
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 1 WHERE id = ?"));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("row");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id"));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                arrayList6.add((ScriptValue)object7);
                v8 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue12, arrayList6, (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                ScriptValue scriptValue15 = ScriptValue.of((String)("<yellow>" + scriptValue2.asStr() + " is now locked (hidden from the browser)."));
                if (scriptValue14 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                    v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue15.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptValue15);
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList8, (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdReset(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object3);
                    v2 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassPlayer.pg$73_location() : PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("loc", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET world = ?, x = ?, y = ?, z = ? WHERE id = ?"));
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("loc");
            arrayList.add(PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("loc");
            arrayList.add(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL);
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("loc");
            arrayList.add(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("loc");
            arrayList.add(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL);
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder4));
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue14 = ScriptValue.of((String)("<green>\u2714 <white>Moved '" + scriptValue2.asStr() + "' to your current location."));
            if (scriptValue13 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object4);
                v4 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(scriptValue14.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue14);
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue cmdRename(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = var1_1.getClassOrVar("Cmd");
        if (var2_2 != ScriptValue.NULL) {
            var3_3 = new ArrayList<ScriptValue>();
            var3_3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)var2_2, var3_3, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var4_4 = v0 /* !! */ ;
        var0.val("old_name", var4_4);
        var5_5 = ScriptContext.builder().copyFrom(var1_1);
        var5_5.val("name", var4_4);
        if (Warps.warpExists(var5_5).asBool() ^ true) {
            var6_6 = var1_1.getClassOrVar("Player");
            if (var6_6 != ScriptValue.NULL) {
                var7_7 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + var4_4.asStr() + "'."));
                if (var6_6 instanceof ScriptValue.Obj && (var9_9 = (var8_8 = (ScriptValue.Obj)var6_6).instance()) != null && !(var9_9 instanceof PolyClass) && var8_8.typeName().equals("Player")) {
                    var10_10 = new PolyClassPlayer(var9_9);
                    v1 /* !! */  = ScriptValue.of((boolean)var10_10.tm$42_send_message(var7_7.asStr()));
                } else {
                    var11_11 = new ArrayList<ScriptValue>();
                    var11_11.add(var7_7);
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var6_6, var11_11, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var12_12 = ScriptContext.builder().copyFrom(var1_1);
        var12_12.val("name", var4_4);
        if (Warps.canManage(var12_12).asBool() ^ true) {
            var13_13 = var1_1.getClassOrVar("Player");
            if (var13_13 != ScriptValue.NULL) {
                var14_14 = "<red>\u2718 <white>You don't own that warp.";
                if (var13_13 instanceof ScriptValue.Obj && (var16_16 = (var15_15 = (ScriptValue.Obj)var13_13).instance()) != null && !(var16_16 instanceof PolyClass) && var15_15.typeName().equals("Player")) {
                    var17_17 = new PolyClassPlayer(var16_16);
                    v2 /* !! */  = ScriptValue.of((boolean)var17_17.tm$42_send_message(var14_14));
                } else {
                    var18_18 = new ArrayList<ScriptValue>();
                    var18_18.add(ScriptValue.of((String)var14_14));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var13_13, var18_18, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var19_19 = ScriptContext.builder().copyFrom(var1_1);
        var20_20 = var1_1.getClassOrVar("Cmd");
        if (var20_20 != ScriptValue.NULL) {
            var21_21 = new ArrayList<ScriptValue>();
            var21_21.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "new_name"));
            v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)var20_20, var21_21, (ScriptContext)var1_1);
        } else {
            v3 /* !! */  = ScriptValue.NULL;
        }
        var19_19.val("s", v3 /* !! */ );
        var22_22 = Warps.sanitize(var19_19);
        var0.val("new_name", var22_22);
        if (ScriptFormula.valuesEqualStr((ScriptValue)var22_22, (String)"")) ** GOTO lbl-1000
        var23_23 = ScriptContext.builder().copyFrom(var1_1);
        var23_23.val("name", var22_22);
        if (!Warps.warpExists(var23_23).asBool()) {
            v4 = false;
        } else lbl-1000:
        // 2 sources

        {
            v4 = true;
        }
        if (v4) {
            var24_24 = var1_1.getClassOrVar("Player");
            if (var24_24 != ScriptValue.NULL) {
                var25_25 = "<red>\u2718 <white>Could not rename that warp.";
                if (var24_24 instanceof ScriptValue.Obj && (var27_27 = (var26_26 = (ScriptValue.Obj)var24_24).instance()) != null && !(var27_27 instanceof PolyClass) && var26_26.typeName().equals("Player")) {
                    var28_28 = new PolyClassPlayer(var27_27);
                    v5 /* !! */  = ScriptValue.of((boolean)var28_28.tm$42_send_message(var25_25));
                } else {
                    var29_29 = new ArrayList<ScriptValue>();
                    var29_29.add(ScriptValue.of((String)var25_25));
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var24_24, var29_29, (ScriptContext)var1_1);
                }
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var30_30 = var1_1.getClassOrVar("SQL");
        if (var30_30 != ScriptValue.NULL) {
            var31_31 = new ArrayList<ScriptValue>();
            var31_31.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET name = ? WHERE id = ?"));
            var31_31.add(var22_22);
            var32_32 = ScriptContext.builder().copyFrom(var1_1);
            var32_32.val("name", var4_4);
            var31_31.add(Warps.warpId(var32_32));
            v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var30_30, var31_31, (ScriptContext)var1_1);
        } else {
            v6 /* !! */  = ScriptValue.NULL;
        }
        var33_33 = var1_1.getClassOrVar("Player");
        if (var33_33 != ScriptValue.NULL) {
            var34_34 = ScriptValue.of((String)("<green>\u2714 <white>Renamed to '" + var22_22.asStr() + "'."));
            if (var33_33 instanceof ScriptValue.Obj && (var36_36 = (var35_35 = (ScriptValue.Obj)var33_33).instance()) != null && !(var36_36 instanceof PolyClass) && var35_35.typeName().equals("Player")) {
                var37_37 = new PolyClassPlayer(var36_36);
                v7 /* !! */  = ScriptValue.of((boolean)var37_37.tm$42_send_message(var34_34.asStr()));
            } else {
                var38_38 = new ArrayList<ScriptValue>();
                var38_38.add(var34_34);
                v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var33_33, var38_38, (ScriptContext)var1_1);
            }
        } else {
            v7 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdSetowner(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("target", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET owner_uuid = ?, owner_name = ? WHERE id = ?"));
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("target");
            arrayList.add((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("target");
            arrayList.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            arrayList.add(Warps.warpId(builder4));
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue12;
            ScriptValue scriptValue13 = ScriptValue.of((String)("<green>\u2714 <white>Transferred " + scriptValue2.asStr() + " to " + ((scriptValue12 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "."));
            if (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue13.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue13);
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdBanSet(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("target", scriptValue7);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue2);
        ScriptValue scriptValue8 = Warps.warpId(builder4);
        builder.val("wid", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?"));
            arrayList.add(scriptValue8);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("target");
            arrayList.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("SQL");
        if (scriptValue11 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_bans (warp_id, uuid, name) VALUES (?, ?, ?)"));
            arrayList.add(scriptValue8);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("target");
            arrayList.add((ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("target");
            arrayList.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL));
            v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue15;
            ScriptValue scriptValue16 = ScriptValue.of((String)("<green>\u2714 <white>Banned " + ((scriptValue15 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " from " + scriptValue2.asStr() + "."));
            if (scriptValue14 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue16.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue16);
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdBanRemove(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object3 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object2;
        builder.val("player_name", scriptValue7);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue2);
        ScriptValue scriptValue8 = Warps.warpId(builder4);
        builder.val("wid", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name FROM warp_bans WHERE warp_id = ? AND lower(name) = ?"));
            arrayList.add(scriptValue8);
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptValue7);
            arrayList.add(ScriptFormula.callBuiltin((String)"lower", arrayList2, (ScriptContext)scriptContext));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object;
        builder.val("rows", scriptValue10);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue10);
        if (ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptValue scriptValue12 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue7.asStr() + " isn't banned from that warp."));
                if (scriptValue11 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                    v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue12.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue12);
                    v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("SQL");
        if (scriptValue13 != ScriptValue.NULL) {
            Object object7;
            ArrayList<Object> arrayList4 = new ArrayList<Object>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?"));
            arrayList4.add(scriptValue8);
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("rows");
            if (scriptValue14 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
                object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, arrayList6, (ScriptContext)scriptContext);
            } else {
                object7 = ScriptValue.NULL;
            }
            arrayList4.add(PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)object7, arrayList5, (ScriptContext)scriptContext));
            v7 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue13, arrayList4, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            Object object9;
            StringBuilder stringBuilder = new StringBuilder().append("<green>\u2714 <white>Unbanned ");
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("rows");
            if (scriptValue16 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0));
                object9 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, arrayList8, (ScriptContext)scriptContext);
            } else {
                object9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue17 = ScriptValue.of((String)stringBuilder.append(PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)object9, arrayList7, (ScriptContext)scriptContext).asStr()).append(".").toString());
            if (scriptValue15 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                v10 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue17.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(scriptValue17);
                v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, arrayList9, (ScriptContext)scriptContext);
            }
        } else {
            v10 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdFavourite(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue4);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        Warps.toggleFavourite(builder3);
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdRemoveall(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("PERM_ADMIN_REMOVEALL");
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object3 = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                object3 = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        if (object3.asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "<red>\u2718 <white>You don't have permission to do that.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("player_name", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue7 = scriptValue5;
            if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object = polyClassServer.tm$10_get_player(scriptValue7.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue7);
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("target", scriptValue8);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue8);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            Object object7;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
            if (scriptValue9 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE owner_uuid = ?"));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("target");
                arrayList3.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
                object7 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue9, arrayList3, (ScriptContext)scriptContext);
            } else {
                object7 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object7);
            ScriptValue scriptValue11 = ScriptFormula.callBuiltin((String)"len", arrayList2, (ScriptContext)scriptContext);
            builder.val("n", scriptValue11);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("SQL");
            if (scriptValue12 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE owner_uuid = ?"));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("target");
                arrayList4.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL));
                v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue12, arrayList4, (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
        } else {
            Object object8;
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("SQL");
            if (scriptValue14 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE lower(owner_name) = ?"));
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptValue5);
                arrayList6.add(ScriptFormula.callBuiltin((String)"lower", arrayList7, (ScriptContext)scriptContext));
                object8 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue14, arrayList6, (ScriptContext)scriptContext);
            } else {
                object8 = ScriptValue.NULL;
            }
            arrayList5.add((ScriptValue)object8);
            ScriptValue scriptValue15 = ScriptFormula.callBuiltin((String)"len", arrayList5, (ScriptContext)scriptContext);
            builder.val("n", scriptValue15);
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("SQL");
            if (scriptValue16 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE lower(owner_name) = ?"));
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(scriptValue5);
                arrayList8.add(ScriptFormula.callBuiltin((String)"lower", arrayList9, (ScriptContext)scriptContext));
                v7 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue16, arrayList8, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Player");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptValue scriptValue18 = ScriptValue.of((String)("<green>\u2714 <white>Removed " + scriptContext.getStr("n") + " warp(s) owned by " + scriptValue5.asStr() + "."));
            if (scriptValue17 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object9);
                v8 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue18.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                arrayList10.add(scriptValue18);
                v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, arrayList10, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdReload(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("PERM_ADMIN_RELOAD");
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                object = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't have permission to do that.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "cepolyfill reload scripts";
            if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object4);
                v2 = ScriptValue.of((boolean)polyClassServer.tm$14_exec_command(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v2 = PolyDispatch.bootstrapCall("memberCall", "exec_command", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "cepolyfill reload cmds";
            if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object5);
                v3 = ScriptValue.of((boolean)polyClassServer.tm$14_exec_command(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v3 = PolyDispatch.bootstrapCall("memberCall", "exec_command", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "<green>\u2714 <white>Reloaded warps scripts/commands.";
            if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdAddwarps(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("PERM_ADMIN_ADDWARPS");
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object3 = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                object3 = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        if (object3.asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "<red>\u2718 <white>You don't have permission to do that.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("target", scriptValue5);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "amount"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue6, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        arrayList.add((ScriptValue)object);
        ScriptValue scriptValue7 = ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext);
        builder.val("n", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("target");
        if (scriptValue8 != ScriptValue.NULL) {
            Object object6;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps_extra_slots"));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "int"));
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("target");
            if (scriptValue9 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps_extra_slots"));
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "int"));
                object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, arrayList4, (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)object6, (ScriptValue)scriptValue7));
            v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, arrayList3, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptValue scriptValue11;
            ScriptValue scriptValue12 = ScriptValue.of((String)("<green>\u2714 <white>Gave " + ((scriptValue11 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " " + scriptValue7.asStr() + " extra warp slot(s)."));
            if (scriptValue10 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue12.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue12);
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "all"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "houses"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "shops"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "farms"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "pvp"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "other"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "event"));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("CATEGORIES", (ScriptValue)array);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "oldest"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visits"));
        ScriptValue.Array array2 = new ScriptValue.Array(arrayList2);
        builder.val("SORT_MODES", (ScriptValue)array2);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 9.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 45.0));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext);
        builder.val("WARP_SLOTS_BROWSE", scriptValue);
        double d = 36.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)36.0);
        builder.val("WARP_PAGE_SIZE_BROWSE", scriptValue2);
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 28.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 29.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 30.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 31.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 32.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 33.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 34.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 37.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 38.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 39.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 40.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 41.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 42.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 43.0));
        ScriptValue.Array array3 = new ScriptValue.Array(arrayList4);
        builder.val("WARP_SLOTS_LIST", (ScriptValue)array3);
        double d2 = 14.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)14.0);
        builder.val("WARP_PAGE_SIZE_LIST", scriptValue3);
        double d3 = 2.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)2.0);
        builder.val("WARP_FREE_LIMIT", scriptValue4);
        ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "polyfills.warps.extra");
        builder.val("WARP_SPONSOR_PERMISSION", scriptValue5);
        ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "polyfills.warps.admin.bypass");
        builder.val("PERM_ADMIN_BYPASS", scriptValue6);
        ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "polyfills.warps.admin.removeall");
        builder.val("PERM_ADMIN_REMOVEALL", scriptValue7);
        ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "polyfills.warps.admin.reload");
        builder.val("PERM_ADMIN_RELOAD", scriptValue8);
        ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "polyfills.warps.admin.addwarps");
        builder.val("PERM_ADMIN_ADDWARPS", scriptValue9);
        double d4 = 30.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)30.0);
        builder.val("WARP_CREATE_COST", scriptValue10);
        double d5 = 5.0;
        ScriptValue scriptValue11 = ScriptValue.of((double)5.0);
        builder.val("WARP_TELEPORT_COST", scriptValue11);
        double d6 = 500.0;
        ScriptValue scriptValue12 = ScriptValue.of((double)500.0);
        builder.val("WARP_EXTRA_SLOT_COST", scriptValue12);
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 47.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 48.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 49.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 50.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 51.0));
        ScriptValue.Array array4 = new ScriptValue.Array(arrayList5);
        builder.val("SPONSOR_SLOTS", (ScriptValue)array4);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1500.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 900.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 600.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 400.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 200.0));
        ScriptValue.Array array5 = new ScriptValue.Array(arrayList6);
        builder.val("SPONSOR_PRICES", (ScriptValue)array5);
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 172800.0));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 86400.0));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 43200.0));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 21600.0));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 3600.0));
        ScriptValue.Array array6 = new ScriptValue.Array(arrayList7);
        builder.val("SPONSOR_DURATIONS", (ScriptValue)array6);
        FILE_SCOPE = builder.build();
    }
}
