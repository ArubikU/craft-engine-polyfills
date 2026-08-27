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
                    object = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "close_inventory", (ScriptValue)scriptValue, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue sanitize(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.callBuiltin1((String)"trim", (ScriptValue)scriptContext.getClassOrVar("s"), (ScriptContext)scriptContext);
    }

    public static ScriptValue arrContains(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("arr"));
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                builder.val("x", scriptValue);
                if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("val"))) continue;
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
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("target", scriptValue3);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool()) {
            Object object4;
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
                    object4 = PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            return PolyDispatch.bootstrapCall("memberCall", "with_profile", (ScriptValue)object4, (ScriptValue)scriptContext.getClassOrVar("name"), (ScriptContext)scriptContext);
        }
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
                object = PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return PolyDispatch.bootstrapCall("memberCall", "with_profile", (ScriptValue)object, (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
    }

    public static ScriptValue warpId(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE name = ?")), (ScriptValue)scriptContext.getClassOrVar("name"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("rows", scriptValue2);
        if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "");
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("rows");
        return PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext);
    }

    public static ScriptValue warpRow(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT * FROM warps WHERE name = ?")), (ScriptValue)scriptContext.getClassOrVar("name"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("rows", scriptValue2);
        if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() <= 0.0) {
            return ScriptFormula.callBuiltin0((String)"make_map", (ScriptContext)scriptContext);
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("rows");
        return scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue allWarpNames(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT name FROM warps ORDER BY created_at ASC")), (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                builder.val("row", scriptValue2);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue4 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("out", scriptValue4);
                array2 = scriptValue4;
            }
        }
        return array2;
    }

    public static ScriptValue warpExists(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warps WHERE name = ?")), (ScriptValue)scriptContext.getClassOrVar("name"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.of((ScriptFormula.callBuiltin1((String)"len", (ScriptValue)object, (ScriptContext)scriptContext).asNum() > 0.0 ? 1 : 0) != 0);
    }

    public static ScriptValue ownedWarpNames(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT name FROM warps WHERE owner_uuid = ? ORDER BY created_at ASC")), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("row", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("out", scriptValue5);
                array2 = scriptValue5;
            }
        }
        return array2;
    }

    public static ScriptValue isOwner(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptContext.getClassOrVar("name"));
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        return ScriptValue.of((boolean)ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder2), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "owner_uuid")), (ScriptContext)scriptContext), (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL))));
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
        var3_3 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player");
        var5_5 = var3_3 != null ? var3_3.pg$73_location() : ((var4_4 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)var4_4, (ScriptContext)var1_1) : ScriptValue.NULL);
        var0.val("loc", var5_5);
        var6_6 = var1_1.getClassOrVar("SQL");
        if (var6_6 != ScriptValue.NULL) {
            var7_7 = new ArrayList<Object>();
            var7_7.add(ScriptValue.of((String)("INSERT INTO warps (id, name, owner_uuid, owner_name, world, x, y, z, created_at) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")));
            var8_8 = var1_1.getClassOrVar("Uuid");
            var7_7.add(var8_8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "random", (ScriptValue)var8_8, (ScriptContext)var1_1) : ScriptValue.NULL);
            var7_7.add(var1_1.getClassOrVar("name"));
            var9_9 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player");
            var7_7.add(var9_9 != null ? var9_9.pg$34_uuid() : ((var10_10 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var10_10, (ScriptContext)var1_1) : ScriptValue.NULL));
            var11_11 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player");
            var7_7.add(var11_11 != null ? var11_11.pg$67_name() : ((var12_12 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var12_12, (ScriptContext)var1_1) : ScriptValue.NULL));
            var7_7.add(PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)(var5_5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
            var7_7.add(var5_5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL);
            var7_7.add(var5_5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL);
            var7_7.add(var5_5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL);
            var13_13 = PolyClassServer.ofVar((ScriptContext)var1_1, (String)"Server");
            var7_7.add(var13_13 != null ? var13_13.pg$16_time() : ((var14_14 = var1_1.getClassOrVar("Server")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)var14_14, (ScriptContext)var1_1) : ScriptValue.NULL));
            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var6_6, var7_7, (ScriptContext)var1_1);
        } else {
            v1 /* !! */  = ScriptValue.NULL;
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
            ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE id = ?");
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("name", scriptContext.getClassOrVar("name"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)Warps.warpId(builder3), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 1);
    }

    public static ScriptValue isBanned(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2;
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warp_bans WHERE warp_id = ? AND uuid = ?");
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", scriptContext.getClassOrVar("name"));
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)scriptValue3, (ScriptValue)Warps.warpId(builder2), (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return ScriptValue.of((ScriptFormula.callBuiltin1((String)"len", (ScriptValue)object, (ScriptContext)scriptContext).asNum() > 0.0 ? 1 : 0) != 0);
    }

    public static ScriptValue isFavourite(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2;
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warp_favourites WHERE warp_id = ? AND player_uuid = ?");
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", scriptContext.getClassOrVar("name"));
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)scriptValue3, (ScriptValue)Warps.warpId(builder2), (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return ScriptValue.of((ScriptFormula.callBuiltin1((String)"len", (ScriptValue)object, (ScriptContext)scriptContext).asNum() > 0.0 ? 1 : 0) != 0);
    }

    public static ScriptValue playerFavouriteIds(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT warp_id FROM warp_favourites WHERE player_uuid = ?")), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("row", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warp_id")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("out", scriptValue5);
                array2 = scriptValue5;
            }
        }
        return array2;
    }

    public static ScriptValue playerBannedIds(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT warp_id FROM warp_bans WHERE uuid = ?")), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("row", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warp_id")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("out", scriptValue5);
                array2 = scriptValue5;
            }
        }
        return array2;
    }

    public static ScriptValue recordVisit(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptContext.getClassOrVar("name"));
        ScriptValue scriptValue2 = Warps.warpId(builder2);
        builder.val("wid", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT count FROM warp_visitors WHERE warp_id = ? AND uuid = ?")), (ScriptValue)scriptValue2, (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("existing", scriptValue4);
        if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum() > 0.0) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("SQL");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue scriptValue6;
                ScriptValue scriptValue7;
                PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
                Object object = polyClassPlayer2 != null ? polyClassPlayer2.pg$67_name() : ((scriptValue7 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
                v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warp_visitors SET count = count + 1, name = ? WHERE warp_id = ? AND uuid = ?")), (ScriptValue)object, (ScriptValue)scriptValue2, (ScriptValue)(polyClassPlayer3 != null ? polyClassPlayer3.pg$34_uuid() : ((scriptValue6 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue scriptValue9;
                ScriptValue scriptValue10;
                PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
                Object object = polyClassPlayer4 != null ? polyClassPlayer4.pg$34_uuid() : ((scriptValue10 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL);
                PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
                v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_visitors (warp_id, uuid, name, count) VALUES (?, ?, ?, 1)")), (ScriptValue)scriptValue2, (ScriptValue)object, (ScriptValue)(polyClassPlayer5 != null ? polyClassPlayer5.pg$67_name() : ((scriptValue9 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
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
        var4_4 = var1_1.getClassOrVar("data");
        if (ScriptFormula.callBuiltin1((String)"is_null", (ScriptValue)(var4_4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var4_4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asBool()) {
            var5_5 = var1_1.getClassOrVar("Player");
            if (var5_5 != ScriptValue.NULL) {
                var6_6 = "<red>\u2718 <white>That warp no longer exists.";
                if (var5_5 instanceof ScriptValue.Obj && (var8_8 = (var7_7 = (ScriptValue.Obj)var5_5).instance()) != null && !(var8_8 instanceof PolyClass) && var7_7.typeName().equals("Player")) {
                    var9_9 = new PolyClassPlayer(var8_8);
                    v0 /* !! */  = ScriptValue.of((boolean)var9_9.tm$42_send_message(var6_6));
                } else {
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var5_5, (ScriptValue)ScriptValue.of((String)var6_6), (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var10_10 = var1_1.getClassOrVar("SQL");
        v1 /* !! */  = var10_10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)var10_10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT 1 FROM warp_bans WHERE warp_id = ? AND uuid = ?")), (ScriptValue)((var11_11 = var1_1.getClassOrVar("data")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var11_11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)((var12_12 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player")) != null ? var12_12.pg$34_uuid() : ((var13_13 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var13_13, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptContext)var1_1) : ScriptValue.NULL;
        if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)v1 /* !! */ , (ScriptContext)var1_1).asNum() > 0.0) {
            var14_14 = var1_1.getClassOrVar("Player");
            if (var14_14 != ScriptValue.NULL) {
                var15_15 = "<red>\u2718 <white>You are banned from that warp.";
                if (var14_14 instanceof ScriptValue.Obj && (var17_17 = (var16_16 = (ScriptValue.Obj)var14_14).instance()) != null && !(var17_17 instanceof PolyClass) && var16_16.typeName().equals("Player")) {
                    var18_18 = new PolyClassPlayer(var17_17);
                    v2 /* !! */  = ScriptValue.of((boolean)var18_18.tm$42_send_message(var15_15));
                } else {
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var14_14, (ScriptValue)ScriptValue.of((String)var15_15), (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var19_19 = var1_1.getClassOrVar("data");
        var20_20 = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)(var19_19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var19_19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "world")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("tw", var20_20);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var20_20, (ScriptContext)var1_1).asBool()) {
            var21_21 = var1_1.getClassOrVar("Player");
            if (var21_21 != ScriptValue.NULL) {
                var22_22 = "<red>\u2718 <white>That warp's world is not currently loaded.";
                if (var21_21 instanceof ScriptValue.Obj && (var24_24 = (var23_23 = (ScriptValue.Obj)var21_21).instance()) != null && !(var24_24 instanceof PolyClass) && var23_23.typeName().equals("Player")) {
                    var25_25 = new PolyClassPlayer(var24_24);
                    v3 /* !! */  = ScriptValue.of((boolean)var25_25.tm$42_send_message(var22_22));
                } else {
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var21_21, (ScriptValue)ScriptValue.of((String)var22_22), (ScriptContext)var1_1);
                }
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var26_26 = var1_1.getClassOrVar("data");
        var29_29 = ScriptFormula.valuesEqual((ScriptValue)(var26_26 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var26_26, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "owner_uuid")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)((var27_27 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player")) != null ? var27_27.pg$34_uuid() : ((var28_28 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var28_28, (ScriptContext)var1_1) : ScriptValue.NULL)));
        var30_30 = ScriptValue.of((boolean)var29_29);
        var0.val("is_own_warp", var30_30);
        if (!(var29_29 ^ true)) ** GOTO lbl-1000
        var31_31 = var1_1.getClassOrVar("Player");
        if (var31_31 != ScriptValue.NULL) {
            var32_32 = var1_1.getClassOrVar("WARP_TELEPORT_COST");
            if (var31_31 instanceof ScriptValue.Obj && (var34_34 = (var33_33 = (ScriptValue.Obj)var31_31).instance()) != null && !(var34_34 instanceof PolyClass) && var33_33.typeName().equals("Player")) {
                var35_35 = new PolyClassPlayer(var34_34);
                v4 /* !! */  = ScriptValue.of((boolean)var35_35.tm$28_take_exp(var32_32.asNum()));
            } else {
                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)var31_31, (ScriptValue)var32_32, (ScriptContext)var1_1);
            }
        } else {
            v4 /* !! */  = ScriptValue.NULL;
        }
        if (v4 /* !! */ .asBool() ^ true) {
            v5 = true;
        } else lbl-1000:
        // 2 sources

        {
            v5 = false;
        }
        if (v5) {
            var36_36 = var1_1.getClassOrVar("Player");
            if (var36_36 != ScriptValue.NULL) {
                var37_37 = ScriptValue.of((String)("<red>\u2718 <white>You need " + var1_1.getStr("WARP_TELEPORT_COST") + " XP points to use a warp."));
                if (var36_36 instanceof ScriptValue.Obj && (var39_39 = (var38_38 = (ScriptValue.Obj)var36_36).instance()) != null && !(var39_39 instanceof PolyClass) && var38_38.typeName().equals("Player")) {
                    var40_40 = new PolyClassPlayer(var39_39);
                    v6 /* !! */  = ScriptValue.of((boolean)var40_40.tm$42_send_message(var37_37.asStr()));
                } else {
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var36_36, (ScriptValue)var37_37, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var41_41 = var1_1.getClassOrVar("tw");
        if (var41_41 != ScriptValue.NULL) {
            var42_42 = var1_1.getClassOrVar("data");
            v7 /* !! */  = var42_42 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var42_42, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "x")), (ScriptContext)var1_1) : ScriptValue.NULL;
            var43_43 = var1_1.getClassOrVar("data");
            var44_44 = var1_1.getClassOrVar("data");
            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var41_41, (ScriptValue)v7 /* !! */ , (ScriptValue)(var43_43 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var43_43, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "y")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)(var44_44 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var44_44, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "z")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        } else {
            v8 /* !! */  = ScriptValue.NULL;
        }
        var45_45 = v8 /* !! */ ;
        var0.val("loc", var45_45);
        var46_46 = var1_1.getClassOrVar("Player");
        if (var46_46 != ScriptValue.NULL) {
            var47_47 = var45_45;
            if (var46_46 instanceof ScriptValue.Obj && (var49_49 = (var48_48 = (ScriptValue.Obj)var46_46).instance()) != null && !(var49_49 instanceof PolyClass) && var48_48.typeName().equals("Player")) {
                var50_50 = new PolyClassPlayer(var49_49);
                v9 /* !! */  = ScriptValue.of((boolean)var50_50.tm$24_teleport_to(var47_47));
            } else {
                v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var46_46, (ScriptValue)var47_47, (ScriptContext)var1_1);
            }
        } else {
            v9 /* !! */  = ScriptValue.NULL;
        }
        var51_51 = var1_1.getClassOrVar("SQL");
        v10 /* !! */  = var51_51 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var51_51, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET visits = visits + 1 WHERE id = ?")), (ScriptValue)((var52_52 = var1_1.getClassOrVar("data")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var52_52, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
        var53_53 = ScriptContext.builder().copyFrom(var1_1);
        var53_53.val("name", var1_1.getClassOrVar("name"));
        Warps.recordVisit(var53_53);
        var54_54 = var1_1.getClassOrVar("Player");
        if (var54_54 != ScriptValue.NULL) {
            var55_55 = ScriptValue.of((String)("<green>\u2714 <white>Welcome to <yellow>" + var1_1.getStr("name") + "<white>!"));
            if (var54_54 instanceof ScriptValue.Obj && (var57_57 = (var56_56 = (ScriptValue.Obj)var54_54).instance()) != null && !(var57_57 instanceof PolyClass) && var56_56.typeName().equals("Player")) {
                var58_58 = new PolyClassPlayer(var57_57);
                v11 /* !! */  = ScriptValue.of((boolean)var58_58.tm$42_send_message(var55_55.asStr()));
            } else {
                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var54_54, (ScriptValue)var55_55, (ScriptContext)var1_1);
            }
        } else {
            v11 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue warpIconOf(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("row");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("icon", scriptValue2);
        if (ScriptFormula.callBuiltin1((String)"is_null", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool() || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "minecraft:ender_pearl");
        }
        return scriptValue2;
    }

    public static ScriptValue warpLoreOf(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("row");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "desc")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("desc", scriptValue2);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("lore", (ScriptValue)array);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("row");
        ScriptValue scriptValue4 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)ScriptValue.of((String)("<gray>Owner: <white>" + (scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "owner_name")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
        builder.val("lore", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("row");
        ScriptValue scriptValue6 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)ScriptValue.of((String)("<gray>World: <white>" + (scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "world")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
        builder.val("lore", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)ScriptValue.of((String)("<gray>Visits: <white>" + (scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visits")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
        builder.val("lore", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
        ScriptValue scriptValue10 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)ScriptValue.of((String)("<gray>Category: <white>" + (scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "category")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
        builder.val("lore", scriptValue10);
        if (ScriptFormula.callBuiltin1((String)"is_null", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool() ^ true && ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true) {
            ScriptValue scriptValue11 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)ScriptValue.of((String)("<gray>Description: <white>" + scriptValue2.asStr())), (ScriptContext)scriptContext);
            builder.val("lore", scriptValue11);
        }
        if (scriptContext.getBool("is_fav")) {
            ScriptValue scriptValue12 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<gold>\u2605 Favourited")), (ScriptContext)scriptContext);
            builder.val("lore", scriptValue12);
        }
        if (scriptContext.getBool("is_ban")) {
            ScriptValue scriptValue13 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>You are banned from this warp")), (ScriptContext)scriptContext);
            builder.val("lore", scriptValue13);
        }
        ScriptValue scriptValue14 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "")), (ScriptContext)scriptContext);
        builder.val("lore", scriptValue14);
        ScriptValue scriptValue15 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<green>Left-click <gray>to teleport")), (ScriptContext)scriptContext);
        builder.val("lore", scriptValue15);
        ScriptValue scriptValue16 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("lore"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<yellow>Right-click <gray>for more options")), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        double d;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("mode", scriptValue2);
        double d2 = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("SORT_MODES"), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                builder.val("i", scriptValue4);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("SORT_MODES");
                if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue2)) continue;
                ScriptValue scriptValue6 = scriptValue4;
                builder.val("idx", scriptValue6);
            }
        }
        double d3 = (d = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("SORT_MODES"), (ScriptContext)scriptContext).asNum()) == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0))).asNum() % d;
        ScriptValue scriptValue7 = ScriptValue.of((double)d3);
        builder.val("next_idx", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue9;
            String string = "warps_sort";
            String string3 = "string";
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("SORT_MODES");
            Object object4 = scriptValue9 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext) : ScriptValue.NULL;
            if (scriptValue8 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue9));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Cmd");
        Object object5 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue11, (ScriptValue)scriptContext.getClassOrVar("page_name"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue openSearchDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        Object object2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search Warps")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value");
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Warp or owner name");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_search";
            String string2 = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object2, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)object, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search")), (ScriptValue)ScriptValue.of((String)("warps.pf:on_search_submit:" + scriptContext.getStr("page_name"))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
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
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
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
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("page_name"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("page_name"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue cycleCategory(ScriptContext.Builder builder) {
        double d;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        double d2 = 0.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("CATEGORIES"), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("i", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("CATEGORIES");
                if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter"))) continue;
                ScriptValue scriptValue7 = scriptValue5;
                builder.val("idx", scriptValue7);
            }
        }
        double d3 = (d = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("CATEGORIES"), (ScriptContext)scriptContext).asNum()) == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0))).asNum() % d;
        ScriptValue scriptValue8 = ScriptValue.of((double)d3);
        builder.val("next_idx", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue10;
            String string = "warps_category_filter";
            String string3 = "string";
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("CATEGORIES");
            Object object4 = scriptValue10 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext) : ScriptValue.NULL;
            if (scriptValue9 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue10));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
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
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string4, scriptValue13));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Cmd");
        Object object6 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_category_filter";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("filter", scriptValue2);
        StringBuilder stringBuilder = new StringBuilder().append("%");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "warps_search";
            String string3 = "string";
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object = polyClassPlayer.tm$12_get_typed(string, string3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)object, (ScriptContext)scriptContext).asStr()).append("%").toString());
        builder.val("like", scriptValue4);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue5 = Warps.sortOrderClause(builder2);
        builder.val("order", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"all")) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
            return scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)("SELECT * FROM warps WHERE locked = 0 AND (lower(name) LIKE ? OR lower(owner_name) LIKE ?) ORDER BY " + scriptValue5.asStr())), (ScriptValue)scriptValue4, (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("SQL");
        return scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)("SELECT * FROM warps WHERE locked = 0 AND category = ? AND (lower(name) LIKE ? OR lower(owner_name) LIKE ?) ORDER BY " + scriptValue5.asStr())), (ScriptValue)scriptValue2, (ScriptValue)scriptValue4, (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue maxBrowseOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.filteredBrowseRows(builder2), (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        double d = scriptContext.getNum("WARP_PAGE_SIZE_BROWSE");
        return ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("WARP_PAGE_SIZE_BROWSE"), (ScriptContext)scriptContext));
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("global_idx");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("name");
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue10 : list) {
                builder.val("i", scriptValue10);
                ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue10);
                builder.val("global_idx", scriptValue11);
                scriptValue7 = scriptValue11;
                if (scriptValue7.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("rows");
                ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("row", scriptValue13);
                scriptValue9 = scriptValue13;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("name", scriptValue15);
                scriptValue8 = scriptValue15;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("WARP_SLOTS_BROWSE");
                arrayList2.add((ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("row", scriptValue9);
                arrayList2.add(Warps.warpIconOf(builder5));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList2.add(ScriptValue.of((String)("<yellow>" + scriptValue8.asStr())));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("row", scriptValue9);
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("arr", scriptValue4);
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("row");
                builder7.val("val", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder6.val("is_fav", Warps.arrContains(builder7));
                ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                builder8.val("arr", scriptValue5);
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("row");
                builder8.val("val", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder6.val("is_ban", Warps.arrContains(builder8));
                arrayList2.add(Warps.warpLoreOf(builder6));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList2.add(ScriptValue.of((String)("warps.pf:on_browse_click:" + scriptValue8.asStr())));
                ScriptValue scriptValue19 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue19);
                array2 = scriptValue19;
            }
        }
        return array2;
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
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
            Object object = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visitedit")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object2 = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
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
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
                }
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)object5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
            if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue7));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue9 = ScriptValue.of((String)("<green>\u2714 <white>Unlocked an extra warp slot! (" + ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.ownedWarpNames(builder2), (ScriptContext)scriptContext).asStr() + "/" + Warps.effectiveLimit(builder3).asStr() + " used)"));
            if (scriptValue8 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue9.asStr()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mywarpsCountLine(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        return ScriptValue.of((String)("<gray>Current: <white>" + ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.ownedWarpNames(builder2), (ScriptContext)scriptContext).asStr() + "/" + Warps.effectiveLimit(builder3).asStr()));
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
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        builder2.val("name", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL));
        Warps.onCreateSubmit(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue onSetCommand(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        builder2.val("s", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue2 = Warps.sanitize(builder2);
        builder.val("name", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>Warp name cannot be empty.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.warpExists(builder3).asBool()) {
            ScriptValue scriptValue4;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            if (Warps.isOwner(builder4).asBool() ^ true) {
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object;
                    ScriptValue scriptValue6 = ScriptValue.of((String)("<red>\u2718 <white>A warp named '" + scriptValue2.asStr() + "' already exists."));
                    if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                        v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue6.asStr()));
                    } else {
                        v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                    }
                } else {
                    v1 = ScriptValue.NULL;
                }
                return ScriptValue.NULL;
            }
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue7 = polyClassPlayer != null ? polyClassPlayer.pg$73_location() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("loc", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET world = ?, x = ?, y = ?, z = ? WHERE id = ?");
                CallSite callSite = PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                Object object = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                Object object2 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                Object object3 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("name", scriptValue2);
                v7 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptValue)callSite, (ScriptValue)object, (ScriptValue)object2, (ScriptValue)object3, (ScriptValue)Warps.warpId(builder5), (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>Moved '" + scriptValue2.asStr() + "' to your current location."));
                if (scriptValue10 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                    v8 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue11.asStr()));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
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
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.ownedWarpNames(builder4), (ScriptContext)scriptContext).asNum() >= Warps.effectiveLimitNum(builder5).asNum()) {
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
                    object4 = PolyDispatch.bootstrapCall("memberCall", "has_exp", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
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
                        v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
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
                        v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "has_exp", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
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
                    v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
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
                    v7 = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
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
                    v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
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
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue purgeExpiredSponsors(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassServer polyClassServer;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_sponsors WHERE expires_at < ?")), (ScriptValue)((polyClassServer = PolyClassServer.ofVar((ScriptContext)scriptContext, (String)"Server")) != null ? polyClassServer.pg$16_time() : ((scriptValue = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue sponsorRow(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        Warps.purgeExpiredSponsors(builder2);
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)("SELECT w.name AS name, s.buyer_uuid AS buyer_uuid, s.expires_at AS expires_at " + "FROM warp_sponsors s JOIN warps w ON w.id = s.warp_id WHERE s.slot_num = ?")), (ScriptValue)scriptContext.getClassOrVar("slot_num"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("rows", scriptValue2);
        if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() <= 0.0) {
            return ScriptFormula.callBuiltin0((String)"make_map", (ScriptContext)scriptContext);
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("rows");
        return scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue generateSponsorButtons(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("SPONSOR_SLOTS"), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
        ScriptValue scriptValue = scriptContext.getClassOrVar("hours");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("slot_num");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("lore");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("price");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("name");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("slot");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                ScriptValue scriptValue9;
                builder.val("i", scriptValue8);
                ScriptValue scriptValue10 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
                builder.val("slot_num", scriptValue10);
                scriptValue2 = scriptValue10;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("SPONSOR_SLOTS");
                ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("slot", scriptValue12);
                scriptValue6 = scriptValue12;
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("slot_num", scriptValue2);
                ScriptValue scriptValue13 = Warps.sponsorRow(builder2);
                builder.val("row", scriptValue13);
                scriptValue7 = scriptValue13;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("name", scriptValue15);
                scriptValue5 = scriptValue15;
                if (ScriptFormula.callBuiltin1((String)"is_null", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) {
                    ScriptValue scriptValue16;
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("SPONSOR_PRICES");
                    ScriptValue scriptValue18 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("price", scriptValue18);
                    scriptValue4 = scriptValue18;
                    double d = 3600.0;
                    double d2 = 3600.0 == 0.0 ? 0.0 : ((scriptValue16 = scriptContext.getClassOrVar("SPONSOR_DURATIONS")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() / d;
                    ScriptValue scriptValue19 = ScriptValue.of((double)d2);
                    builder.val("hours", scriptValue19);
                    scriptValue = scriptValue19;
                    ArrayList<Object> arrayList2 = new ArrayList<Object>();
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                    arrayList2.add(scriptValue6);
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "minecraft:diamond"));
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                    arrayList2.add(ScriptValue.of((String)("<yellow>Sponsor Slot #" + scriptValue2.asStr())));
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<gray>Feature one of YOUR warps here."));
                    arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                    arrayList3.add(ScriptValue.of((String)("<gray>Price: <white>" + scriptValue4.asStr() + " XP")));
                    arrayList3.add(ScriptValue.of((String)("<gray>Time: <white>" + scriptValue.asStr() + "h")));
                    arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                    arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<green>Left-click to purchase"));
                    arrayList2.add(new ScriptValue.Array(arrayList3));
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                    arrayList2.add(ScriptValue.of((String)("warps.pf:open_sponsor_dialog:" + scriptValue2.asStr())));
                    ScriptValue scriptValue20 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                    builder.val("out", scriptValue20);
                    array2 = scriptValue20;
                    continue;
                }
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue5);
                ScriptValue scriptValue21 = Warps.warpLore(builder3);
                builder.val("lore", scriptValue21);
                scriptValue3 = scriptValue21;
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("row");
                PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
                if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "buyer_uuid")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue9 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL)))) {
                    ScriptValue scriptValue23 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "")), (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue23);
                    scriptValue3 = scriptValue23;
                    ScriptValue scriptValue24 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>Shift-right-click to remove your sponsor")), (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue24);
                    scriptValue3 = scriptValue24;
                }
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                arrayList4.add(scriptValue6);
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("name", scriptValue5);
                arrayList4.add(Warps.warpIcon(builder4));
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList4.add(ScriptValue.of((String)("<gold>[Sponsored] <yellow>" + scriptValue5.asStr())));
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                arrayList4.add(scriptContext.getClassOrVar("lore"));
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList4.add(ScriptValue.of((String)("warps.pf:on_sponsor_click:" + scriptValue2.asStr())));
                ScriptValue scriptValue25 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList4, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue25);
                array2 = scriptValue25;
            }
        }
        return array2;
    }

    public static ScriptValue openSponsorDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)("Sponsor Slot #" + scriptContext.getStr("slot_num"))), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "One of your warp names")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Sponsor")), (ScriptValue)ScriptValue.of((String)("warps.pf:on_sponsor_submit:" + scriptContext.getStr("slot_num"))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onSponsorSubmit(ScriptContext.Builder builder) {
        Object object;
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
                Object object2;
                ScriptValue scriptValue3 = ScriptValue.of((String)("<red>\u2718 <white>You don't own a warp named '" + scriptValue.asStr() + "'."));
                if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue3.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        double d = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("slot_num"), (ScriptContext)scriptContext).asNum() - 1.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("idx", scriptValue4);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("slot_num", ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("slot_num"), (ScriptContext)scriptContext));
        if (ScriptFormula.callBuiltin1((String)"is_null", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.sponsorRow(builder4), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext), (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>That sponsor slot is already taken.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SPONSOR_PRICES");
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("price", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue9 = scriptValue7;
            if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$38_has_exp(scriptValue9.asNum()));
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "has_exp", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ScriptValue scriptValue11 = ScriptValue.of((String)("<red>\u2718 <white>You need " + scriptValue7.asStr() + " XP to sponsor slot #" + scriptContext.getStr("slot_num") + "."));
                if (scriptValue10 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                    v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue13 = scriptValue7;
            if (scriptValue12 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$28_take_exp(scriptValue13.asNum()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "take_exp", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("SQL");
        Object object7 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_sponsors WHERE slot_num = ?")), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("slot_num"), (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("SQL");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptValue scriptValue16;
            ScriptValue scriptValue17;
            ScriptValue scriptValue18 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_sponsors (slot_num, warp_id, buyer_uuid, expires_at) VALUES (?, ?, ?, ?)");
            ScriptValue scriptValue19 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("slot_num"), (ScriptContext)scriptContext);
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue);
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            PolyClassServer polyClassServer = PolyClassServer.ofVar((ScriptContext)scriptContext, (String)"Server");
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("SPONSOR_DURATIONS");
            v8 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue15, (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptValue)Warps.warpId(builder5), (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue17 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassServer != null ? polyClassServer.pg$16_time() : ((scriptValue16 = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("Player");
        if (scriptValue21 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            ScriptValue scriptValue22 = ScriptValue.of((String)("<green>\u2714 <white>Sponsored '" + scriptValue.asStr() + "' on slot #" + scriptContext.getStr("slot_num") + "!"));
            if (scriptValue21 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue22.asStr()));
            } else {
                v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue21, (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
            }
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("Cmd");
        Object object9 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "main")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onSponsorClick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("slot_num", ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("slot_num"), (ScriptContext)scriptContext));
        ScriptValue scriptValue3 = Warps.sponsorRow(builder2);
        builder.val("row", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("row");
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_null", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("MenuClick");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "click_type", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"shift_right") && ScriptFormula.valuesEqual((ScriptValue)((scriptValue2 = scriptContext.getClassOrVar("row")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "buyer_uuid")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)))) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("SQL");
            Object object = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_sponsors WHERE slot_num = ?")), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("slot_num"), (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue9 = ScriptValue.of((String)("<yellow>Removed your sponsor from slot #" + scriptContext.getStr("slot_num") + "."));
                if (scriptValue8 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue9.asStr()));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Cmd");
            Object object3 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "main")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue5);
        Warps.teleportToWarp(builder3);
        return ScriptValue.NULL;
    }

    public static ScriptValue manageTitle(ScriptContext.Builder builder) {
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        return ScriptValue.of((String)("{Images.from('cml:player_warps_submenu_1')}{Images.shift(-170)}<black> " + (polyClassPlayer != null ? polyClassPlayer.tg$68_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())) + "'s Warps"));
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
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)object3, (ScriptContext)scriptContext).asStr()).append("%").toString());
        builder.val("like", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue scriptValue4;
            PolyClassPlayer polyClassPlayer;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            object2 = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)("SELECT * FROM warps WHERE owner_uuid = ? AND (lower(name) LIKE ? OR lower(owner_name) LIKE ?) ORDER BY " + Warps.sortOrderClause(builder2).asStr())), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue2, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("offset", scriptValue9);
        double d = scriptValue9.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue10 = ScriptValue.of((double)d);
        builder.val("start", scriptValue10);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"), (ScriptContext)scriptContext));
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("global_idx");
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("name");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("row");
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue14 : list) {
                builder.val("i", scriptValue14);
                ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue14);
                builder.val("global_idx", scriptValue15);
                scriptValue11 = scriptValue15;
                if (scriptValue11.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("rows");
                ScriptValue scriptValue17 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("row", scriptValue17);
                scriptValue13 = scriptValue17;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("name", scriptValue19);
                scriptValue12 = scriptValue19;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                arrayList2.add((ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("row", scriptValue13);
                arrayList2.add(Warps.warpIconOf(builder5));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList2.add(ScriptValue.of((String)("<yellow>" + scriptValue12.asStr())));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("row", scriptValue13);
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("arr", scriptValue6);
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("row");
                builder7.val("val", (ScriptValue)(scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder6.val("is_fav", Warps.arrContains(builder7));
                ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                builder8.val("arr", scriptValue7);
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("row");
                builder8.val("val", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder6.val("is_ban", Warps.arrContains(builder8));
                arrayList2.add(Warps.warpLoreOf(builder6));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList2.add(ScriptValue.of((String)("warps.pf:on_mywarps_click:" + scriptValue12.asStr())));
                ScriptValue scriptValue23 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue23);
                array2 = scriptValue23;
            }
        }
        return array2;
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
            Object object = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visitedit")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        } else {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
            Object object = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "managewarp")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue maxManageOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.ownedWarpNames(builder2), (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        return ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue favouriteTitle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "{Images.from('cml:player_warps_submenu_1')}{Images.shift(-170)}<black> Favourite Warps");
    }

    public static ScriptValue favouriteRows(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptContext scriptContext = builder.peek();
        StringBuilder stringBuilder = new StringBuilder().append("%");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "warps_search";
            String string2 = "string";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                object = polyClassPlayer2.tm$12_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)object, (ScriptContext)scriptContext).asStr()).append("%").toString());
        builder.val("like", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("SQL");
        return scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)("SELECT w.* FROM warp_favourites f JOIN warps w ON w.id = f.warp_id " + "WHERE f.player_uuid = ? AND (lower(w.name) LIKE ? OR lower(w.owner_name) LIKE ?) ORDER BY w.name ASC")), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue3, (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("offset", scriptValue4);
        double d = scriptValue4.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("start", scriptValue5);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"), (ScriptContext)scriptContext));
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("global_idx");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("name");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("row");
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                builder.val("i", scriptValue9);
                ScriptValue scriptValue10 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue9);
                builder.val("global_idx", scriptValue10);
                scriptValue6 = scriptValue10;
                if (scriptValue6.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("rows");
                ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("row", scriptValue12);
                scriptValue8 = scriptValue12;
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("row");
                ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("name", scriptValue14);
                scriptValue7 = scriptValue14;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                arrayList2.add((ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("row", scriptValue8);
                arrayList2.add(Warps.warpIconOf(builder4));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList2.add(ScriptValue.of((String)("<yellow>" + scriptValue7.asStr())));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("row", scriptValue8);
                builder5.val("is_fav",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Warps.class, 1));
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("arr", scriptValue2);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("row");
                builder6.val("val", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder5.val("is_ban", Warps.arrContains(builder6));
                arrayList2.add(Warps.warpLoreOf(builder5));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList2.add(ScriptValue.of((String)("warps.pf:on_favourite_click:" + scriptValue7.asStr())));
                ScriptValue scriptValue17 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue17);
                array2 = scriptValue17;
            }
        }
        return array2;
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
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
            Object object = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visitedit")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", scriptContext.getClassOrVar("name"));
            Warps.teleportToWarp(builder2);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue maxFavouriteOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.favouriteRows(builder2), (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        return ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "favourite")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "favourite")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
            ScriptValue scriptValue3;
            PolyClassPlayer polyClassPlayer;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("SQL");
            Object object = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_favourites WHERE warp_id = ? AND player_uuid = ?")), (ScriptValue)scriptValue, (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue6 = ScriptValue.of((String)("<yellow>Removed <white>" + scriptContext.getStr("name") + " <yellow>from favourites."));
                if (scriptValue5 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue6.asStr()));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue7;
            PolyClassPlayer polyClassPlayer;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
            Object object = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_favourites (warp_id, player_uuid) VALUES (?, ?)")), (ScriptValue)scriptValue, (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue7 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue10 = ScriptValue.of((String)("<green>Added <white>" + scriptContext.getStr("name") + " <green>to favourites."));
                if (scriptValue9 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object3);
                    v3 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(scriptValue10.asStr()));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        boolean bl = ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder3), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "locked")), (ScriptContext)scriptContext), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)));
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
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Warp Description")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value");
        ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Description");
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue);
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder3), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "desc")), (ScriptContext)scriptContext), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Set")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_desc_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET desc = ? WHERE id = ?");
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("s", scriptContext.getClassOrVar("value"));
            ScriptValue scriptValue4 = Warps.sanitize(builder4);
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue);
            v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)Warps.warpId(builder5), (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Description updated.";
            if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwToggleLock(ScriptContext.Builder builder) {
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
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("data");
        if (ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "locked")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)))) {
            ScriptValue scriptValue4;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("SQL");
            Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 0 WHERE id = ?")), (ScriptValue)((scriptValue4 = scriptContext.getClassOrVar("data")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue7 = ScriptValue.of((String)("<green>\u2714 <white>" + scriptValue.asStr() + " is now public."));
                if (scriptValue6 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue7.asStr()));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue8;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
            Object object = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 1 WHERE id = ?")), (ScriptValue)((scriptValue8 = scriptContext.getClassOrVar("data")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue11 = ScriptValue.of((String)("<yellow>" + scriptValue.asStr() + " is now locked (hidden from the browser)."));
                if (scriptValue10 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "managewarp")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenCategoryDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Warps.currentWarp(builder2);
        builder.val("name", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("MenuClick");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "click_type", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"right")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = 'other' WHERE id = ?");
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue);
                v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)Warps.warpId(builder3), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<yellow>Category removed (reset to 'other').";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Dialog");
        Object object = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Warp Category")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value");
        ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Category");
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue);
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)Warps.warpRow(builder4), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "category")), (ScriptContext)scriptContext), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Set")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_category_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = ? WHERE id = ?");
            ScriptValue scriptValue6 = ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)scriptContext.getClassOrVar("cat"), (ScriptContext)scriptContext);
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue2);
            v2 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue, (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)Warps.warpId(builder5), (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue8 = ScriptValue.of((String)("<green>\u2714 <white>Category set to '" + scriptContext.getStr("cat") + "'."));
            if (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue8.asStr()));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenBanDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Ban Player")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Player Name")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Ban")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_ban_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("target", scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue2.asStr() + " is not online."));
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue7.asStr()));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
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
        Object object4 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?")), (ScriptValue)scriptValue8, (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("SQL");
        Object object5 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_bans (warp_id, uuid, name) VALUES (?, ?, ?)")), (ScriptValue)scriptValue8, (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue12 = ScriptValue.of((String)("<green>\u2714 <white>Banned " + (scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " from " + scriptValue.asStr() + "."));
            if (scriptValue11 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue12.asStr()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwSetIcon(ScriptContext.Builder builder) {
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
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue3 = polyClassPlayer != null ? polyClassPlayer.pg$48_main_hand() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("held", scriptValue3);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>Hold an item first.";
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("SQL");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET icon = ? WHERE id = ?");
            Object object = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)object, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Icon updated.";
            if (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object);
                v4 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenRenameDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rename Warp")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "New Name")), (ScriptValue)Warps.currentWarp(builder2), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rename")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_rename_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var8_8, (ScriptValue)ScriptValue.of((String)var9_9), (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var13_13 = var1_1.getClassOrVar("SQL");
        if (var13_13 != ScriptValue.NULL) {
            v2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET name = ? WHERE id = ?");
            var14_14 = ScriptContext.builder().copyFrom(var1_1);
            var14_14.val("name", var3_3);
            v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var13_13, (ScriptValue)v2, (ScriptValue)var5_5, (ScriptValue)Warps.warpId(var14_14), (ScriptContext)var1_1);
        } else {
            v3 /* !! */  = ScriptValue.NULL;
        }
        var15_15 = var1_1.getClassOrVar("Player");
        if (var15_15 != ScriptValue.NULL) {
            var16_16 = "warps_current_warp";
            var17_17 = "string";
            var18_18 = var5_5;
            if (var15_15 instanceof ScriptValue.Obj && (var20_20 = (var19_19 = (ScriptValue.Obj)var15_15).instance()) != null && !(var20_20 instanceof PolyClass) && var19_19.typeName().equals("Player")) {
                var21_21 = new PolyClassPlayer(var20_20);
                v4 /* !! */  = ScriptValue.of((boolean)var21_21.tm$30_set_typed(var16_16, var17_17, var18_18));
            } else {
                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var15_15, (ScriptValue)ScriptValue.of((String)var16_16), (ScriptValue)ScriptValue.of((String)var17_17), (ScriptValue)var18_18, (ScriptContext)var1_1);
            }
        } else {
            v4 /* !! */  = ScriptValue.NULL;
        }
        var22_22 = var1_1.getClassOrVar("Player");
        if (var22_22 != ScriptValue.NULL) {
            var23_23 = ScriptValue.of((String)("<green>\u2714 <white>Renamed to '" + var5_5.asStr() + "'."));
            if (var22_22 instanceof ScriptValue.Obj && (var25_25 = (var24_24 = (ScriptValue.Obj)var22_22).instance()) != null && !(var25_25 instanceof PolyClass) && var24_24.typeName().equals("Player")) {
                var26_26 = new PolyClassPlayer(var25_25);
                v5 /* !! */  = ScriptValue.of((boolean)var26_26.tm$42_send_message(var23_23.asStr()));
            } else {
                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var22_22, (ScriptValue)var23_23, (ScriptContext)var1_1);
            }
        } else {
            v5 /* !! */  = ScriptValue.NULL;
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
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
            Object object = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue mwOpenTransferDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Transfer Ownership")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "New Owner Name")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Transfer")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_transfer_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("target", scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue7 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue2.asStr() + " is not online."));
                if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue7.asStr()));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET owner_uuid = ?, owner_name = ? WHERE id = ?");
            Object object4 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            Object object5 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue);
            v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptValue)object4, (ScriptValue)object5, (ScriptValue)Warps.warpId(builder5), (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>Transferred " + scriptValue.asStr() + " to " + (scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "."));
            if (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
            } else {
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Cmd");
        Object object7 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "manage")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)object, (ScriptContext)scriptContext);
        builder.val("search", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", Warps.currentWarp(builder3));
        ScriptValue scriptValue3 = Warps.warpId(builder2);
        builder.val("wid", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("SQL");
            ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name FROM warp_bans WHERE warp_id = ? ORDER BY name")), (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("rows", scriptValue5);
        } else {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
            ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name FROM warp_bans WHERE warp_id = ? AND lower(name) LIKE ? ORDER BY name")), (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)("%" + scriptValue2.asStr() + "%")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("rows", scriptValue7);
        }
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("rows"));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                builder.val("row", scriptValue8);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue11 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue11);
                array2 = scriptValue11;
            }
        }
        return array2;
    }

    public static ScriptValue openBannedSearchDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        Object object2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search Banned Players")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value");
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Player name");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_banned_search";
            String string2 = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object2, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)object, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_banned_search_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        double d = scriptValue3.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("start", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"), (ScriptContext)scriptContext));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("entry");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("global_idx");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("target_name");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("target_uuid");
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                builder.val("i", scriptValue9);
                ScriptValue scriptValue10 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue9);
                builder.val("global_idx", scriptValue10);
                scriptValue6 = scriptValue10;
                if (scriptValue6.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("records");
                ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("entry", scriptValue12);
                scriptValue5 = scriptValue12;
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("entry");
                ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("target_uuid", scriptValue14);
                scriptValue8 = scriptValue14;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("entry");
                ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("target_name", scriptValue16);
                scriptValue7 = scriptValue16;
                ArrayList<Object> arrayList2 = new ArrayList<Object>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                arrayList2.add(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue7);
                arrayList2.add(Warps.playerHeadIcon(builder3));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList2.add(ScriptValue.of((String)("<white>" + scriptValue7.asStr())));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                arrayList3.add(ScriptValue.of((String)("<gray>Banned from <white>" + Warps.currentWarp(builder4).asStr())));
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, ""));
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>Left-click to unban"));
                arrayList2.add(new ScriptValue.Array(arrayList3));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList2.add(ScriptValue.of((String)("warps.pf:on_unban_click:" + scriptValue8.asStr())));
                ScriptValue scriptValue18 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue18);
                array2 = scriptValue18;
            }
        }
        return array2;
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
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?");
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue);
            v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)Warps.warpId(builder4), (ScriptValue)scriptContext.getClassOrVar("target_uuid"), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Unbanned.";
            if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue maxBannedOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.bannedRecords(builder2), (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        return ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "banned")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)object, (ScriptContext)scriptContext);
        builder.val("search", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", Warps.currentWarp(builder3));
        ScriptValue scriptValue3 = Warps.warpId(builder2);
        builder.val("wid", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("SQL");
            ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name, count FROM warp_visitors WHERE warp_id = ? ORDER BY name")), (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("rows", scriptValue5);
        } else {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
            ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name, count FROM warp_visitors WHERE warp_id = ? AND lower(name) LIKE ? ORDER BY name")), (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)("%" + scriptValue2.asStr() + "%")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("rows", scriptValue7);
        }
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("rows"));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                builder.val("row", scriptValue8);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue12 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue12);
                array2 = scriptValue12;
            }
        }
        return array2;
    }

    public static ScriptValue openVisitedSearchDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        Object object2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search Visited Players")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "value");
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Player name");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "warps_visited_search";
            String string2 = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object2, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)object, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Search")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_visited_search_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        double d = scriptValue3.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("start", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"), (ScriptContext)scriptContext));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("entry");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("global_idx");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("visitor_name");
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                builder.val("i", scriptValue8);
                ScriptValue scriptValue9 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue8);
                builder.val("global_idx", scriptValue9);
                scriptValue6 = scriptValue9;
                if (scriptValue6.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("records");
                ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("entry", scriptValue11);
                scriptValue5 = scriptValue11;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("entry");
                ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("visitor_name", scriptValue13);
                scriptValue7 = scriptValue13;
                ArrayList<Object> arrayList2 = new ArrayList<Object>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                arrayList2.add(scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue7);
                arrayList2.add(Warps.playerHeadIcon(builder3));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList2.add(ScriptValue.of((String)("<white>" + scriptValue7.asStr())));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("entry");
                arrayList3.add(ScriptValue.of((String)("<gray>Visited <white>" + ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext).asStr() + " <gray>time(s)")));
                arrayList2.add(new ScriptValue.Array(arrayList3));
                ScriptValue scriptValue16 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue16);
                array2 = scriptValue16;
            }
        }
        return array2;
    }

    public static ScriptValue maxVisitedOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.visitorRecords(builder2), (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        return ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "visited")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        Object object3 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("target"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_number", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rate Warp")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Stars (0-5)")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 5.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 5.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Cmd"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "Rate")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps.pf:on_rate_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onRateSubmit(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = Warps.currentWarp(builder2);
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.isOwner(builder3).asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You can't rate your own warp.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("stars"), (ScriptContext)scriptContext);
        builder.val("n", scriptValue4);
        if (scriptValue4.asNum() < 0.0) {
            double d = 0.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
            builder.val("n", scriptValue5);
        }
        if (scriptContext.getNum("n") > 5.0) {
            double d = 5.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)5.0);
            builder.val("n", scriptValue6);
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue2);
        ScriptValue scriptValue7 = Warps.warpId(builder4);
        builder.val("wid", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
        Object object = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_ratings WHERE warp_id = ? AND uuid = ?")), (ScriptValue)scriptValue7, (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue scriptValue10;
            ScriptValue scriptValue11;
            PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            Object object2 = polyClassPlayer3 != null ? polyClassPlayer3.pg$34_uuid() : ((scriptValue11 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_ratings (warp_id, uuid, name, stars) VALUES (?, ?, ?, ?)")), (ScriptValue)scriptValue7, (ScriptValue)object2, (ScriptValue)(polyClassPlayer4 != null ? polyClassPlayer4.pg$67_name() : ((scriptValue10 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptContext.getClassOrVar("n"), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue13 = ScriptValue.of((String)("<green>\u2714 <white>Rated " + scriptValue2.asStr() + " " + scriptContext.getStr("n") + " star(s)."));
            if (scriptValue12 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object3);
                v4 = ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(scriptValue13.asStr()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
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
            ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name, stars FROM warp_ratings WHERE warp_id = ? ORDER BY name");
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", Warps.currentWarp(builder3));
            object = PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)Warps.warpId(builder2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("rows", scriptValue3);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("out", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue3);
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                builder.val("row", scriptValue4);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid"));
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars"));
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
                arrayList2.add((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars")), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue8 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue8);
                array2 = scriptValue8;
            }
        }
        return array2;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("offset", scriptValue3);
        double d = scriptValue3.asNum() * scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("start", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("WARP_PAGE_SIZE_LIST"), (ScriptContext)scriptContext));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("entry");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("rater_uuid");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("global_idx");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("lore");
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("stars");
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("rater_name");
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                ScriptValue scriptValue12;
                builder.val("i", scriptValue11);
                ScriptValue scriptValue13 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue11);
                builder.val("global_idx", scriptValue13);
                scriptValue7 = scriptValue13;
                if (scriptValue7.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("records");
                ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("entry", scriptValue15);
                scriptValue5 = scriptValue15;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("entry");
                ScriptValue scriptValue17 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("rater_uuid", scriptValue17);
                scriptValue6 = scriptValue17;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("entry");
                ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("rater_name", scriptValue19);
                scriptValue10 = scriptValue19;
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("entry");
                ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("stars", scriptValue21);
                scriptValue9 = scriptValue21;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)("<gray>Rating: <yellow>" + scriptValue9.asStr() + "/5")));
                ScriptValue.Array array3 = new ScriptValue.Array(arrayList2);
                builder.val("lore", (ScriptValue)array3);
                scriptValue8 = array3;
                PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue12 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL)))) {
                    ScriptValue scriptValue22 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "")), (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue22);
                    scriptValue8 = scriptValue22;
                    ScriptValue scriptValue23 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "<red>Left-click to remove your rating")), (ScriptContext)scriptContext);
                    builder.val("lore", scriptValue23);
                    scriptValue8 = scriptValue23;
                }
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "slot"));
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("WARP_SLOTS_LIST");
                arrayList3.add((ScriptValue)(scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "icon"));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("name", scriptValue10);
                arrayList3.add(Warps.playerHeadIcon(builder3));
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name"));
                arrayList3.add(ScriptValue.of((String)("<white>" + scriptValue10.asStr() + "'s rate")));
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "lore"));
                arrayList3.add(scriptContext.getClassOrVar("lore"));
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "action"));
                arrayList3.add(ScriptValue.of((String)("warps.pf:on_unrate_click:" + scriptValue6.asStr())));
                ScriptValue scriptValue25 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("out", scriptValue25);
                array2 = scriptValue25;
            }
        }
        return array2;
    }

    public static ScriptValue onUnrateClick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rater_uuid"), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL))) ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("SQL");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_ratings WHERE warp_id = ? AND uuid = ?");
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("name", Warps.currentWarp(builder3));
            v1 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)Warps.warpId(builder2), (ScriptValue)scriptContext.getClassOrVar("rater_uuid"), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
        Object object = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "rate")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue maxRateOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.ratingRecords(builder2), (ScriptContext)scriptContext);
        builder.val("n", scriptValue);
        if (scriptValue.asNum() <= 0.0) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0);
        }
        double d = scriptContext.getNum("WARP_PAGE_SIZE_LIST");
        return ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)Math.floor(d == 0.0 ? 0.0 : (scriptValue.asNum() - 1.0) / d)), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "rate")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("Cmd")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_page", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "rate")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v13 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v14 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v15 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v16 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v16 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdRemove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE id = ?");
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue9 = ScriptValue.of((String)("<green>\u2714 <white>Deleted warp '" + scriptValue2.asStr() + "'."));
            if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue9.asStr()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdDescSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET desc = ? WHERE id = ?");
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Cmd");
            builder4.val("s", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "text")), (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptValue scriptValue9 = Warps.sanitize(builder4);
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue2);
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)scriptValue9, (ScriptValue)Warps.warpId(builder5), (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Description updated.";
            if (scriptValue10 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdDescRemove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET desc = '' WHERE id = ?");
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Description cleared.";
            if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue warpNamesSummary(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("SQL");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT name FROM warps WHERE owner_uuid = ? ORDER BY created_at ASC")), (ScriptValue)scriptContext.getClassOrVar("owner_uuid"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("rows", scriptValue2);
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "");
        builder.val("names", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue2);
        ScriptValue scriptValue4 = scriptValue3;
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                ScriptValue scriptValue6;
                builder.val("row", scriptValue5);
                if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"")) {
                    ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
                    ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("names", scriptValue8);
                    scriptValue4 = scriptValue8;
                    continue;
                }
                ScriptValue scriptValue9 = ScriptValue.of((String)(scriptValue4.asStr() + "<gray>, <white>" + ((scriptValue6 = scriptContext.getClassOrVar("row")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()));
                builder.val("names", scriptValue9);
                scriptValue4 = scriptValue9;
            }
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count"));
        arrayList.add(ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue2, (ScriptContext)scriptContext));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "names"));
        arrayList.add(scriptValue4);
        return ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue cmdList(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        builder2.val("owner_uuid", (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        ScriptValue scriptValue2 = Warps.warpNamesSummary(builder2);
        builder.val("info", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("info");
        Object object = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (ScriptFormula.callBuiltin1((String)"int", (ScriptValue)object, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<yellow>You have no warps.";
                if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue6;
            StringBuilder stringBuilder = new StringBuilder().append("<yellow>Your warps (").append(((scriptValue6 = scriptContext.getClassOrVar("info")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).append("): <white>");
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("info");
            ScriptValue scriptValue8 = ScriptValue.of((String)stringBuilder.append((scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "names")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).toString());
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object3);
                v3 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(scriptValue8.asStr()));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdListof(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("owner_uuid", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue3 = Warps.warpNamesSummary(builder2);
        builder.val("info", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("info");
        Object object = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (ScriptFormula.callBuiltin1((String)"int", (ScriptValue)object, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue6 = ScriptValue.of((String)("<yellow>" + (scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " has no warps."));
                if (scriptValue5 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue6.asStr()));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("info");
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("info");
            ScriptValue scriptValue10 = ScriptValue.of((String)("<yellow>" + (scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "'s warps (" + (scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "count")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "): <white>" + (scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "names")), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()));
            if (scriptValue7 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue10.asStr()));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdAmount(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue2 = ScriptValue.of((String)("<yellow>You have <white>" + ScriptFormula.callBuiltin1((String)"len", (ScriptValue)Warps.ownedWarpNames(builder2), (ScriptContext)scriptContext).asStr() + "/" + Warps.effectiveLimit(builder3).asStr() + "<yellow> warps."));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue2.asStr()));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdAmountof(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("SQL");
        ScriptValue scriptValue4 = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE owner_uuid = ?")), (ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("n", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue6 = ScriptValue.of((String)("<yellow>" + (scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " has <white>" + scriptValue4.asStr() + "<yellow> warp(s)."));
            if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue6.asStr()));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdIconSet(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue3);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue5 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue3.asStr() + "'."));
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue5.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue3);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue7 = polyClassPlayer != null ? polyClassPlayer.pg$48_main_hand() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("held", scriptValue7);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue7, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>Hold an item first.";
                if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                    v2 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET icon = ? WHERE id = ?");
            Object object = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue3);
            v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptValue)object, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Icon updated.";
            if (scriptValue11 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object);
                v6 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
            } else {
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdIconRemove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET icon = '' WHERE id = ?");
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>\u2714 <white>Icon reset to default.";
            if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdCategorySet(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue3);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue5 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue3.asStr() + "'."));
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue5.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue3);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
        builder4.val("s", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "category")), (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue8 = Warps.sanitize(builder4);
        builder.val("cat", scriptValue8);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue8, (String)"")) {
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "other");
            builder.val("cat", scriptValue9);
        }
        if ((scriptValue = scriptContext.getClassOrVar("SQL")) != ScriptValue.NULL) {
            ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = ? WHERE id = ?");
            ScriptValue scriptValue11 = ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)scriptContext.getClassOrVar("cat"), (ScriptContext)scriptContext);
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("name", scriptValue3);
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue, (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)Warps.warpId(builder5), (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue13 = ScriptValue.of((String)("<green>\u2714 <white>Category set to '" + scriptContext.getStr("cat") + "'."));
            if (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue13.asStr()));
            } else {
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdCategoryRemove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("SQL");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET category = 'other' WHERE id = ?");
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            v3 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<yellow>Category reset to 'other'.";
            if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdRate(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue3);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue5 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue3.asStr() + "'."));
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue5.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue3);
        if (Warps.isOwner(builder3).asBool()) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You can't rate your own warp.";
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "stars")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("n", scriptValue8);
        if (scriptValue8.asNum() < 0.0) {
            double d = 0.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)0.0);
            builder.val("n", scriptValue9);
        }
        if (scriptContext.getNum("n") > 5.0) {
            double d = 5.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)5.0);
            builder.val("n", scriptValue10);
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue3);
        ScriptValue scriptValue11 = Warps.warpId(builder4);
        builder.val("wid", scriptValue11);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("SQL");
        Object object = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_ratings WHERE warp_id = ? AND uuid = ?")), (ScriptValue)scriptValue11, (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$34_uuid() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("SQL");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptValue scriptValue14;
            ScriptValue scriptValue15;
            PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            Object object2 = polyClassPlayer4 != null ? polyClassPlayer4.pg$34_uuid() : ((scriptValue15 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            v4 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_ratings (warp_id, uuid, name, stars) VALUES (?, ?, ?, ?)")), (ScriptValue)scriptValue11, (ScriptValue)object2, (ScriptValue)(polyClassPlayer5 != null ? polyClassPlayer5.pg$67_name() : ((scriptValue14 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptContext.getClassOrVar("n"), (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue17 = ScriptValue.of((String)("<green>\u2714 <white>Rated " + scriptValue3.asStr() + " " + scriptContext.getStr("n") + " star(s)."));
            if (scriptValue16 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object3);
                v5 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(scriptValue17.asStr()));
            } else {
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdLock(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue2);
        ScriptValue scriptValue6 = Warps.warpRow(builder4);
        builder.val("row", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("row");
        if (ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "locked")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1.0)))) {
            ScriptValue scriptValue8;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
            Object object = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 0 WHERE id = ?")), (ScriptValue)((scriptValue8 = scriptContext.getClassOrVar("row")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>" + scriptValue2.asStr() + " is now public."));
                if (scriptValue10 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue12;
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("SQL");
            Object object = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET locked = 1 WHERE id = ?")), (ScriptValue)((scriptValue12 = scriptContext.getClassOrVar("row")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "id")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue15 = ScriptValue.of((String)("<yellow>" + scriptValue2.asStr() + " is now locked (hidden from the browser)."));
                if (scriptValue14 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue15.asStr()));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdReset(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue3);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue5 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue3.asStr() + "'."));
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue5.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue3);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue7 = polyClassPlayer != null ? polyClassPlayer.pg$73_location() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("loc", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET world = ?, x = ?, y = ?, z = ? WHERE id = ?");
            CallSite callSite = PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
            Object object = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            Object object2 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            Object object3 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue3);
            v7 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptValue)callSite, (ScriptValue)object, (ScriptValue)object2, (ScriptValue)object3, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>Moved '" + scriptValue3.asStr() + "' to your current location."));
            if (scriptValue10 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                v8 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue11.asStr()));
            } else {
                v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
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
        var3_3 = var2_2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)var2_2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("old_name", var3_3);
        var4_4 = ScriptContext.builder().copyFrom(var1_1);
        var4_4.val("name", var3_3);
        if (Warps.warpExists(var4_4).asBool() ^ true) {
            var5_5 = var1_1.getClassOrVar("Player");
            if (var5_5 != ScriptValue.NULL) {
                var6_6 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + var3_3.asStr() + "'."));
                if (var5_5 instanceof ScriptValue.Obj && (var8_8 = (var7_7 = (ScriptValue.Obj)var5_5).instance()) != null && !(var8_8 instanceof PolyClass) && var7_7.typeName().equals("Player")) {
                    var9_9 = new PolyClassPlayer(var8_8);
                    v0 /* !! */  = ScriptValue.of((boolean)var9_9.tm$42_send_message(var6_6.asStr()));
                } else {
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var5_5, (ScriptValue)var6_6, (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var10_10 = ScriptContext.builder().copyFrom(var1_1);
        var10_10.val("name", var3_3);
        if (Warps.canManage(var10_10).asBool() ^ true) {
            var11_11 = var1_1.getClassOrVar("Player");
            if (var11_11 != ScriptValue.NULL) {
                var12_12 = "<red>\u2718 <white>You don't own that warp.";
                if (var11_11 instanceof ScriptValue.Obj && (var14_14 = (var13_13 = (ScriptValue.Obj)var11_11).instance()) != null && !(var14_14 instanceof PolyClass) && var13_13.typeName().equals("Player")) {
                    var15_15 = new PolyClassPlayer(var14_14);
                    v1 /* !! */  = ScriptValue.of((boolean)var15_15.tm$42_send_message(var12_12));
                } else {
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var11_11, (ScriptValue)ScriptValue.of((String)var12_12), (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var16_16 = ScriptContext.builder().copyFrom(var1_1);
        var17_17 = var1_1.getClassOrVar("Cmd");
        var16_16.val("s", (ScriptValue)(var17_17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)var17_17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "new_name")), (ScriptContext)var1_1) : ScriptValue.NULL));
        var18_18 = Warps.sanitize(var16_16);
        var0.val("new_name", var18_18);
        if (ScriptFormula.valuesEqualStr((ScriptValue)var18_18, (String)"")) ** GOTO lbl-1000
        var19_19 = ScriptContext.builder().copyFrom(var1_1);
        var19_19.val("name", var18_18);
        if (!Warps.warpExists(var19_19).asBool()) {
            v2 = false;
        } else lbl-1000:
        // 2 sources

        {
            v2 = true;
        }
        if (v2) {
            var20_20 = var1_1.getClassOrVar("Player");
            if (var20_20 != ScriptValue.NULL) {
                var21_21 = "<red>\u2718 <white>Could not rename that warp.";
                if (var20_20 instanceof ScriptValue.Obj && (var23_23 = (var22_22 = (ScriptValue.Obj)var20_20).instance()) != null && !(var23_23 instanceof PolyClass) && var22_22.typeName().equals("Player")) {
                    var24_24 = new PolyClassPlayer(var23_23);
                    v3 /* !! */  = ScriptValue.of((boolean)var24_24.tm$42_send_message(var21_21));
                } else {
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var20_20, (ScriptValue)ScriptValue.of((String)var21_21), (ScriptContext)var1_1);
                }
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var25_25 = var1_1.getClassOrVar("SQL");
        if (var25_25 != ScriptValue.NULL) {
            v4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET name = ? WHERE id = ?");
            var26_26 = ScriptContext.builder().copyFrom(var1_1);
            var26_26.val("name", var3_3);
            v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)var25_25, (ScriptValue)v4, (ScriptValue)var18_18, (ScriptValue)Warps.warpId(var26_26), (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        var27_27 = var1_1.getClassOrVar("Player");
        if (var27_27 != ScriptValue.NULL) {
            var28_28 = ScriptValue.of((String)("<green>\u2714 <white>Renamed to '" + var18_18.asStr() + "'."));
            if (var27_27 instanceof ScriptValue.Obj && (var30_30 = (var29_29 = (ScriptValue.Obj)var27_27).instance()) != null && !(var30_30 instanceof PolyClass) && var29_29.typeName().equals("Player")) {
                var31_31 = new PolyClassPlayer(var30_30);
                v6 /* !! */  = ScriptValue.of((boolean)var31_31.tm$42_send_message(var28_28.asStr()));
            } else {
                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var27_27, (ScriptValue)var28_28, (ScriptContext)var1_1);
            }
        } else {
            v6 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdSetowner(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("SQL");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "UPDATE warps SET owner_uuid = ?, owner_name = ? WHERE id = ?");
            Object object = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            Object object2 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("name", scriptValue2);
            v5 = PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptValue)object, (ScriptValue)object2, (ScriptValue)Warps.warpId(builder4), (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>Transferred " + scriptValue2.asStr() + " to " + (scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "."));
            if (scriptValue10 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
            } else {
                v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdBanSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue2);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue7);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue2);
        ScriptValue scriptValue8 = Warps.warpId(builder4);
        builder.val("wid", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
        Object object = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?")), (ScriptValue)scriptValue8, (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("SQL");
        Object object2 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "INSERT INTO warp_bans (warp_id, uuid, name) VALUES (?, ?, ?)")), (ScriptValue)scriptValue8, (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue12 = ScriptValue.of((String)("<green>\u2714 <white>Banned " + (scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " from " + scriptValue2.asStr() + "."));
            if (scriptValue11 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue12.asStr()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdBanRemove(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue3);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue3);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue5 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue3.asStr() + "'."));
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue5.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("name", scriptValue3);
        if (Warps.canManage(builder3).asBool() ^ true) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You don't own that warp.";
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("player_name", scriptValue8);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("name", scriptValue3);
        ScriptValue scriptValue9 = Warps.warpId(builder4);
        builder.val("wid", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("SQL");
        ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT uuid, name FROM warp_bans WHERE warp_id = ? AND lower(name) = ?")), (ScriptValue)scriptValue9, (ScriptValue)ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)scriptValue8, (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("rows", scriptValue11);
        if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum() <= 0.0) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue13 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue8.asStr() + " isn't banned from that warp."));
                if (scriptValue12 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue13.asStr()));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("SQL");
        Object object = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warp_bans WHERE warp_id = ? AND uuid = ?")), (ScriptValue)scriptValue9, (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)((scriptValue = scriptContext.getClassOrVar("rows")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "uuid")), (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue16;
            ScriptValue scriptValue17 = ScriptValue.of((String)("<green>\u2714 <white>Unbanned " + PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)((scriptValue16 = scriptContext.getClassOrVar("rows")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext).asStr() + "."));
            if (scriptValue15 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue17.asStr()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdFavourite(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "name")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("name", scriptValue2);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("name", scriptValue2);
        if (Warps.warpExists(builder2).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue4 = ScriptValue.of((String)("<red>\u2718 <white>No warp named '" + scriptValue2.asStr() + "'."));
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue4.asStr()));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("PERM_ADMIN_REMOVEALL");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue2.asStr()));
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>You don't have permission to do that.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("player_name", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue7 = scriptValue5;
            if (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object5);
                object = polyClassServer.tm$10_get_player(scriptValue7.asStr());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("target", scriptValue8);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("SQL");
            ScriptValue scriptValue10 = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE owner_uuid = ?")), (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
            builder.val("n", scriptValue10);
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("SQL");
            Object object6 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE owner_uuid = ?")), (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        } else {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("SQL");
            ScriptValue scriptValue13 = ScriptFormula.callBuiltin1((String)"len", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "query", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "SELECT id FROM warps WHERE lower(owner_name) = ?")), (ScriptValue)ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)scriptValue5, (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
            builder.val("n", scriptValue13);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("SQL");
            Object object7 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "execute", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "DELETE FROM warps WHERE lower(owner_name) = ?")), (ScriptValue)ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)scriptValue5, (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            ScriptValue scriptValue16 = ScriptValue.of((String)("<green>\u2714 <white>Removed " + scriptContext.getStr("n") + " warp(s) owned by " + scriptValue5.asStr() + "."));
            if (scriptValue15 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue16.asStr()));
            } else {
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
            }
        } else {
            v5 = ScriptValue.NULL;
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
                object = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v2 = PolyDispatch.bootstrapCall("memberCall", "exec_command", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v3 = PolyDispatch.bootstrapCall("memberCall", "exec_command", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue cmdAddwarps(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("PERM_ADMIN_ADDWARPS");
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$4_has_permission(scriptValue3.asStr()));
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "has_permission", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>You don't have permission to do that.";
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "player")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "amount")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("n", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("target");
        Object object4 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps_extra_slots")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "int")), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)((scriptValue = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "warps_extra_slots")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Warps.class, "int")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue8), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue11 = ScriptValue.of((String)("<green>\u2714 <white>Gave " + (scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " " + scriptValue8.asStr() + " extra warp slot(s)."));
            if (scriptValue10 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue11.asStr()));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
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
        ScriptValue scriptValue = ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 9.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 45.0)), (ScriptContext)scriptContext);
        builder.val("WARP_SLOTS_BROWSE", scriptValue);
        double d = 36.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)36.0);
        builder.val("WARP_PAGE_SIZE_BROWSE", scriptValue2);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 28.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 29.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 30.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 31.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 32.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 33.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 34.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 37.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 38.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 39.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 40.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 41.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 42.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 43.0));
        ScriptValue.Array array3 = new ScriptValue.Array(arrayList3);
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
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 47.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 48.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 49.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 50.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 51.0));
        ScriptValue.Array array4 = new ScriptValue.Array(arrayList4);
        builder.val("SPONSOR_SLOTS", (ScriptValue)array4);
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 1500.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 900.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 600.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 400.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 200.0));
        ScriptValue.Array array5 = new ScriptValue.Array(arrayList5);
        builder.val("SPONSOR_PRICES", (ScriptValue)array5);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 172800.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 86400.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 43200.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 21600.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Warps.class, 3600.0));
        ScriptValue.Array array6 = new ScriptValue.Array(arrayList6);
        builder.val("SPONSOR_DURATIONS", (ScriptValue)array6);
        FILE_SCOPE = builder.build();
    }
}
