package dev.arubik.craftengine.machine.render;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Script-driven keyframe animation engine.
 *
 * Supported entity types:
 *   item_display    — vanilla item_display with full transformation support
 *   text_display    — text with MiniMessage + {expr} scripting, bg color, opacity, shadow, etc.
 *   block_display   — vanilla block_display
 *   armor_stand     — armor stand with equip slots, small/invisible/marker
 *   bettermodel     — BetterModel plugin animated model
 *   modelengine     — ModelEngine plugin animated model
 *   particle        — continuous particle emitter while this display is "alive"
 *
 * Text / item / blockId fields support {expr} scripting evaluated with the
 * ScriptContext passed to tickOn() each server tick.
 * Text fields support MiniMessage tags (the evaluated result is parsed by MiniMessage).
 *
 * 26 easing functions: linear, quad/cubic/quart/sine/expo/circ (in/out/in-out),
 *   back/elastic/bounce (in/out/in-out), step_start, step_end.
 *
 * Additional features: per-axis scale, pivot offset, billboard, brightness override,
 * glow, NMS client-side interpolation, fractional-tick speed, pause/resume/seek,
 * sound events, child animations, on_end/on_loop script callbacks.
 */
public final class ScriptAnimation {

    // ===================================================================
    // EASING
    // ===================================================================

    public enum Easing {
        LINEAR,
        QUAD_IN, QUAD_OUT, QUAD_IN_OUT,
        CUBIC_IN, CUBIC_OUT, CUBIC_IN_OUT,
        QUART_IN, QUART_OUT, QUART_IN_OUT,
        SINE_IN, SINE_OUT, SINE_IN_OUT,
        EXPO_IN, EXPO_OUT, EXPO_IN_OUT,
        CIRC_IN, CIRC_OUT, CIRC_IN_OUT,
        BACK_IN, BACK_OUT, BACK_IN_OUT,
        ELASTIC_IN, ELASTIC_OUT, ELASTIC_IN_OUT,
        BOUNCE_IN, BOUNCE_OUT, BOUNCE_IN_OUT,
        STEP_START, STEP_END;

        public float apply(float t) {
            t = Math.max(0f, Math.min(1f, t));
            return switch (this) {
                case LINEAR       -> t;
                case QUAD_IN      -> t * t;
                case QUAD_OUT     -> t * (2 - t);
                case QUAD_IN_OUT  -> t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
                case CUBIC_IN     -> t * t * t;
                case CUBIC_OUT    -> { float u = t - 1; yield u * u * u + 1; }
                case CUBIC_IN_OUT -> t < 0.5f ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
                case QUART_IN     -> t * t * t * t;
                case QUART_OUT    -> { float u = t - 1; yield 1 - u * u * u * u; }
                case QUART_IN_OUT -> t < 0.5f ? 8 * t * t * t * t : 1 - 8 * (t - 1) * (t - 1) * (t - 1) * (t - 1);
                case SINE_IN      -> (float)(1 - Math.cos(t * Math.PI / 2));
                case SINE_OUT     -> (float)Math.sin(t * Math.PI / 2);
                case SINE_IN_OUT  -> (float)(-(Math.cos(Math.PI * t) - 1) / 2);
                case EXPO_IN      -> t == 0 ? 0 : (float)Math.pow(2, 10 * t - 10);
                case EXPO_OUT     -> t == 1 ? 1 : (float)(1 - Math.pow(2, -10 * t));
                case EXPO_IN_OUT  -> t == 0 ? 0 : t == 1 ? 1 : t < 0.5f
                    ? (float)(Math.pow(2, 20 * t - 10) / 2) : (float)((2 - Math.pow(2, -20 * t + 10)) / 2);
                case CIRC_IN      -> (float)(1 - Math.sqrt(1 - t * t));
                case CIRC_OUT     -> (float)Math.sqrt(1 - (t - 1) * (t - 1));
                case CIRC_IN_OUT  -> t < 0.5f
                    ? (float)((1 - Math.sqrt(1 - 4 * t * t)) / 2)
                    : (float)((Math.sqrt(1 - (-2 * t + 2) * (-2 * t + 2)) + 1) / 2);
                case BACK_IN      -> { float c1 = 1.70158f, c3 = c1 + 1; yield c3 * t * t * t - c1 * t * t; }
                case BACK_OUT     -> { float c1 = 1.70158f, c3 = c1 + 1; float u = t - 1; yield 1 + c3 * u * u * u + c1 * u * u; }
                case BACK_IN_OUT  -> { float c1 = 1.70158f, c2 = c1 * 1.525f;
                    yield t < 0.5f
                        ? (float)((Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2)
                        : (float)((Math.pow(2 * t - 2, 2) * ((c2 + 1) * (2 * t - 2) + c2) + 2) / 2); }
                case ELASTIC_IN   -> t == 0 ? 0 : t == 1 ? 1
                    : (float)(-Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * TWO_PI_OVER_3));
                case ELASTIC_OUT  -> t == 0 ? 0 : t == 1 ? 1
                    : (float)(Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * TWO_PI_OVER_3) + 1);
                case ELASTIC_IN_OUT -> t == 0 ? 0 : t == 1 ? 1 : t < 0.5f
                    ? (float)(-(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * TWO_PI_OVER_4_5)) / 2)
                    : (float)(Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * TWO_PI_OVER_4_5) / 2 + 1);
                case BOUNCE_IN    -> 1 - BOUNCE_OUT.apply(1 - t);
                case BOUNCE_OUT   -> bounceOut(t);
                case BOUNCE_IN_OUT -> t < 0.5f ? (1 - bounceOut(1 - 2 * t)) / 2 : (1 + bounceOut(2 * t - 1)) / 2;
                case STEP_START   -> t <= 0 ? 0 : 1;
                case STEP_END     -> t < 1 ? 0 : 1;
            };
        }

        private static final double TWO_PI_OVER_3   = (2 * Math.PI) / 3;
        private static final double TWO_PI_OVER_4_5 = (2 * Math.PI) / 4.5;

        private static float bounceOut(float t) {
            float n1 = 7.5625f, d1 = 2.75f;
            if      (t < 1 / d1)      return n1 * t * t;
            else if (t < 2 / d1)    { t -= 1.5f  / d1; return n1 * t * t + 0.75f;     }
            else if (t < 2.5 / d1)  { t -= 2.25f / d1; return n1 * t * t + 0.9375f;   }
            else                    { t -= 2.625f/ d1; return n1 * t * t + 0.984375f;  }
        }

        public static Easing parse(String s) {
            if (s == null || s.isBlank()) return LINEAR;
            try { return valueOf(s.toUpperCase(Locale.ROOT)); }
            catch (IllegalArgumentException ignored) { return LINEAR; }
        }
    }

    // ===================================================================
    // DISPLAY STATE
    // ===================================================================

    public static final class DisplayState {

        // --- Entity type ---
        /** "item_display"|"text_display"|"block_display"|"armor_stand"|"bettermodel"|"modelengine"|"particle" */
        public final String type;

        // --- item_display ---
        /** Vanilla item ID or CraftEngine custom item ID. Supports {expr} scripting. */
        public final String item;

        // --- text_display ---
        /** Text content; supports MiniMessage tags and {expr} interpolation. */
        public final String text;
        public final int textBgColor;       // ARGB packed, -1 = default
        public final float textOpacity;     // 0.0–1.0, -1 = default
        public final boolean textShadow;
        public final boolean textSeeThrough;
        public final String textAlignment;  // "left"|"center"|"right"
        public final int textLineWidth;     // -1 = default (200)

        // --- block_display ---
        /** Block state string e.g. "minecraft:stone_slab[type=top]". Supports {expr}. */
        public final String blockId;

        // --- armor_stand ---
        public final String asHeadItem, asChestItem, asLegsItem, asFeetItem;
        public final String asMainHand, asOffHand;
        public final boolean asSmall, asInvisible, asMarker;
        public final float yaw; // body yaw degrees

        // --- bettermodel / modelengine ---
        public final String modelId;
        public final String modelAnimation;
        public final float modelSpeed;

        // --- particle ---
        public final String particleType;   // minecraft particle id
        public final int particleCount;
        public final float particleSpreadX, particleSpreadY, particleSpreadZ;
        public final float particleSpeed;
        public final float particleDirX, particleDirY, particleDirZ;
        public final String particleShape;  // "point"|"sphere"|"sphere_surface"|"cube"|"circle"

        // --- Transform (all types) ---
        public final float px, py, pz;      // position relative to origin
        public final float rx, ry, rz;      // euler rotation degrees (YXZ)
        public final float sx, sy, sz;      // per-axis scale
        public final float pivotX, pivotY, pivotZ; // pivot for transformation matrix

        // --- Display entity properties (item/text/block) ---
        public final String billboard;      // "fixed"|"vertical"|"horizontal"|"center"
        public final boolean glow;
        public final int brightnessBlock, brightnessSky;
        public final float viewRange, shadowRadius, shadowStrength;

        @SuppressWarnings("checkstyle:ParameterNumber")
        public DisplayState(
                String type, String item,
                String text, int textBgColor, float textOpacity,
                boolean textShadow, boolean textSeeThrough, String textAlignment, int textLineWidth,
                String blockId,
                String asHeadItem, String asChestItem, String asLegsItem, String asFeetItem,
                String asMainHand, String asOffHand, boolean asSmall, boolean asInvisible, boolean asMarker,
                float yaw,
                String modelId, String modelAnimation, float modelSpeed,
                String particleType, int particleCount,
                float particleSpreadX, float particleSpreadY, float particleSpreadZ, float particleSpeed,
                float particleDirX, float particleDirY, float particleDirZ, String particleShape,
                float px, float py, float pz,
                float rx, float ry, float rz,
                float sx, float sy, float sz,
                float pivotX, float pivotY, float pivotZ,
                String billboard, boolean glow, int brightnessBlock, int brightnessSky,
                float viewRange, float shadowRadius, float shadowStrength) {
            this.type = type != null ? type : "item_display";
            this.item = item; this.text = text != null ? text : "";
            this.textBgColor = textBgColor; this.textOpacity = textOpacity;
            this.textShadow = textShadow; this.textSeeThrough = textSeeThrough;
            this.textAlignment = textAlignment != null ? textAlignment : "center";
            this.textLineWidth = textLineWidth; this.blockId = blockId;
            this.asHeadItem = asHeadItem; this.asChestItem = asChestItem;
            this.asLegsItem = asLegsItem; this.asFeetItem = asFeetItem;
            this.asMainHand = asMainHand; this.asOffHand = asOffHand;
            this.asSmall = asSmall; this.asInvisible = asInvisible; this.asMarker = asMarker;
            this.yaw = yaw;
            this.modelId = modelId; this.modelAnimation = modelAnimation;
            this.modelSpeed = modelSpeed <= 0 ? 1 : modelSpeed;
            this.particleType = particleType; this.particleCount = Math.max(0, particleCount);
            this.particleSpreadX = particleSpreadX; this.particleSpreadY = particleSpreadY;
            this.particleSpreadZ = particleSpreadZ; this.particleSpeed = particleSpeed;
            this.particleDirX = particleDirX; this.particleDirY = particleDirY; this.particleDirZ = particleDirZ;
            this.particleShape = particleShape != null ? particleShape : "point";
            this.px = px; this.py = py; this.pz = pz;
            this.rx = rx; this.ry = ry; this.rz = rz;
            this.sx = sx <= 0 ? 1 : sx; this.sy = sy <= 0 ? 1 : sy; this.sz = sz <= 0 ? 1 : sz;
            this.pivotX = pivotX; this.pivotY = pivotY; this.pivotZ = pivotZ;
            this.billboard = billboard != null ? billboard : "fixed"; this.glow = glow;
            this.brightnessBlock = brightnessBlock; this.brightnessSky = brightnessSky;
            this.viewRange = viewRange; this.shadowRadius = shadowRadius; this.shadowStrength = shadowStrength;
        }

        public DisplayState lerp(DisplayState other, float t) {
            boolean snap = t >= 0.5f;
            return new DisplayState(
                snap ? other.type : type,
                snap ? other.item : item,
                snap ? other.text : text,
                lerpARGB(textBgColor, other.textBgColor, t),
                lerp(textOpacity, other.textOpacity, t),
                snap ? other.textShadow : textShadow,
                snap ? other.textSeeThrough : textSeeThrough,
                snap ? other.textAlignment : textAlignment,
                snap ? other.textLineWidth : textLineWidth,
                snap ? other.blockId : blockId,
                // armor stand — snap
                snap ? other.asHeadItem : asHeadItem, snap ? other.asChestItem : asChestItem,
                snap ? other.asLegsItem : asLegsItem, snap ? other.asFeetItem  : asFeetItem,
                snap ? other.asMainHand : asMainHand, snap ? other.asOffHand  : asOffHand,
                snap ? other.asSmall    : asSmall,    snap ? other.asInvisible : asInvisible,
                snap ? other.asMarker   : asMarker,
                lerpAngle(yaw, other.yaw, t),
                // model — snap name/anim, lerp speed
                snap ? other.modelId        : modelId,
                snap ? other.modelAnimation : modelAnimation,
                lerp(modelSpeed, other.modelSpeed, t),
                // particle — snap type/shape, lerp numbers
                snap ? other.particleType  : particleType,
                Math.round(lerp(particleCount, other.particleCount, t)),
                lerp(particleSpreadX, other.particleSpreadX, t),
                lerp(particleSpreadY, other.particleSpreadY, t),
                lerp(particleSpreadZ, other.particleSpreadZ, t),
                lerp(particleSpeed,   other.particleSpeed, t),
                lerp(particleDirX,    other.particleDirX, t),
                lerp(particleDirY,    other.particleDirY, t),
                lerp(particleDirZ,    other.particleDirZ, t),
                snap ? other.particleShape : particleShape,
                // transform
                lerp(px, other.px, t), lerp(py, other.py, t), lerp(pz, other.pz, t),
                lerp(rx, other.rx, t), lerp(ry, other.ry, t), lerp(rz, other.rz, t),
                lerp(sx, other.sx, t), lerp(sy, other.sy, t), lerp(sz, other.sz, t),
                lerp(pivotX, other.pivotX, t), lerp(pivotY, other.pivotY, t), lerp(pivotZ, other.pivotZ, t),
                // display props
                snap ? other.billboard : billboard, snap ? other.glow : glow,
                lerpClamped(brightnessBlock, other.brightnessBlock, t, 0, 15),
                lerpClamped(brightnessSky,   other.brightnessSky,   t, 0, 15),
                lerp(viewRange, other.viewRange, t),
                lerp(shadowRadius, other.shadowRadius, t),
                lerp(shadowStrength, other.shadowStrength, t)
            );
        }

        private static float lerp(float a, float b, float t) { return a + (b - a) * t; }
        private static float lerpAngle(float a, float b, float t) {
            float diff = ((b - a) % 360 + 540) % 360 - 180;
            return a + diff * t;
        }
        private static int lerpClamped(int a, int b, float t, int min, int max) {
            if (a < 0 || b < 0) return a < 0 ? b : a;
            return Math.min(max, Math.max(min, Math.round(a + (b - a) * t)));
        }
        private static int lerpARGB(int a, int b, float t) {
            if (a < 0 || b < 0) return t >= 0.5f ? b : a;
            int aa=(a>>24)&0xFF,ra=(a>>16)&0xFF,ga=(a>>8)&0xFF,ba=a&0xFF;
            int ab=(b>>24)&0xFF,rb=(b>>16)&0xFF,gb=(b>>8)&0xFF,bb=b&0xFF;
            return (Math.round(aa+(ab-aa)*t)<<24)|(Math.round(ra+(rb-ra)*t)<<16)
                  |(Math.round(ga+(gb-ga)*t)<<8)| Math.round(ba+(bb-ba)*t);
        }

        // ---- Builder ----
        public static final class Builder {
            String type = "item_display";
            String item = null; String text = ""; int textBgColor = -1;
            float textOpacity = -1; boolean textShadow; boolean textSeeThrough;
            String textAlignment = "center"; int textLineWidth = -1;
            String blockId = null;
            String asHeadItem,asChestItem,asLegsItem,asFeetItem,asMainHand,asOffHand;
            boolean asSmall,asInvisible,asMarker; float yaw;
            String modelId; String modelAnimation; float modelSpeed = 1;
            String particleType; int particleCount = 1;
            float particleSpreadX,particleSpreadY,particleSpreadZ,particleSpeed=0.05f;
            float particleDirX,particleDirY,particleDirZ; String particleShape="point";
            float px,py,pz,rx,ry,rz;
            float sx=1,sy=1,sz=1;
            float pivotX,pivotY,pivotZ;
            String billboard="fixed"; boolean glow;
            int brightnessBlock=-1,brightnessSky=-1;
            float viewRange=0,shadowRadius=-1,shadowStrength=-1;

            public Builder type(String v)   { type=v; return this; }
            public Builder item(String v)   { item=v; return this; }
            public Builder text(String v)   { text=v; return this; }
            public Builder textBg(int v)    { textBgColor=v; return this; }
            public Builder textOpacity(float v){ textOpacity=v; return this; }
            public Builder textShadow(boolean v){ textShadow=v; return this; }
            public Builder textSeeThrough(boolean v){ textSeeThrough=v; return this; }
            public Builder textAlignment(String v){ textAlignment=v; return this; }
            public Builder textLineWidth(int v){ textLineWidth=v; return this; }
            public Builder blockId(String v) { blockId=v; return this; }
            public Builder asHead(String v) { asHeadItem=v; return this; }
            public Builder asChest(String v){ asChestItem=v; return this; }
            public Builder asLegs(String v) { asLegsItem=v; return this; }
            public Builder asFeet(String v) { asFeetItem=v; return this; }
            public Builder asMainHand(String v){ asMainHand=v; return this; }
            public Builder asOffHand(String v) { asOffHand=v; return this; }
            public Builder asSmall(boolean v){ asSmall=v; return this; }
            public Builder asInvisible(boolean v){ asInvisible=v; return this; }
            public Builder asMarker(boolean v){ asMarker=v; return this; }
            public Builder yaw(float v){ yaw=v; return this; }
            public Builder modelId(String v){ modelId=v; return this; }
            public Builder modelAnimation(String v){ modelAnimation=v; return this; }
            public Builder modelSpeed(float v){ modelSpeed=v; return this; }
            public Builder particleType(String v){ particleType=v; return this; }
            public Builder particleCount(int v){ particleCount=v; return this; }
            public Builder particleSpread(float x,float y,float z){ particleSpreadX=x;particleSpreadY=y;particleSpreadZ=z; return this; }
            public Builder particleSpeed(float v){ particleSpeed=v; return this; }
            public Builder particleDir(float x,float y,float z){ particleDirX=x;particleDirY=y;particleDirZ=z; return this; }
            public Builder particleShape(String v){ particleShape=v; return this; }
            public Builder pos(float x,float y,float z){ px=x;py=y;pz=z; return this; }
            public Builder rot(float x,float y,float z){ rx=x;ry=y;rz=z; return this; }
            public Builder scale(float x,float y,float z){ sx=x;sy=y;sz=z; return this; }
            public Builder scale(float u){ sx=sy=sz=u; return this; }
            public Builder pivot(float x,float y,float z){ pivotX=x;pivotY=y;pivotZ=z; return this; }
            public Builder billboard(String v){ billboard=v; return this; }
            public Builder glow(boolean v){ glow=v; return this; }
            public Builder brightness(int block,int sky){ brightnessBlock=block;brightnessSky=sky; return this; }
            public Builder viewRange(float v){ viewRange=v; return this; }
            public Builder shadowRadius(float v){ shadowRadius=v; return this; }
            public Builder shadowStrength(float v){ shadowStrength=v; return this; }

            public DisplayState build() {
                return new DisplayState(type,item,text,textBgColor,textOpacity,textShadow,textSeeThrough,
                    textAlignment,textLineWidth,blockId,
                    asHeadItem,asChestItem,asLegsItem,asFeetItem,asMainHand,asOffHand,
                    asSmall,asInvisible,asMarker,yaw,
                    modelId,modelAnimation,modelSpeed,
                    particleType,particleCount,particleSpreadX,particleSpreadY,particleSpreadZ,particleSpeed,
                    particleDirX,particleDirY,particleDirZ,particleShape,
                    px,py,pz,rx,ry,rz,sx,sy,sz,pivotX,pivotY,pivotZ,
                    billboard,glow,brightnessBlock,brightnessSky,viewRange,shadowRadius,shadowStrength);
            }
        }
    }

    // ===================================================================
    // KEYFRAME / SOUND
    // ===================================================================

    public static final class Keyframe {
        public final int tick;
        public final Map<String, DisplayState> displays;
        public final Easing easing;
        public Keyframe(int tick, Map<String, DisplayState> displays, Easing easing) {
            this.tick = tick; this.displays = displays;
            this.easing = easing != null ? easing : Easing.LINEAR;
        }
    }

    public static final class SoundSpec {
        public final String soundId;
        public final float volume, pitch;
        public SoundSpec(String soundId, float volume, float pitch) {
            this.soundId = soundId; this.volume = volume; this.pitch = pitch;
        }
    }

    // ===================================================================
    // FIELDS
    // ===================================================================

    private final List<Keyframe> keyframes   = new ArrayList<>();
    private final Map<String, UUID> entities = new ConcurrentHashMap<>();

    // Model plugin renderers keyed by display id
    private final Map<String, BetterModelMachineRenderer>  bmRenderers = new ConcurrentHashMap<>();
    private final Map<String, ModelEngineMachineRenderer>  meRenderers = new ConcurrentHashMap<>();

    private int currentTick      = 0;
    private float fractionalTick = 0f;
    private boolean playing      = false;
    private boolean paused       = false;
    private boolean loop         = false;
    private float speed          = 1.0f;
    private int loopCount        = 0;
    private int maxLoops         = -1;
    private int interpolationDuration = 2;

    @Nullable private dev.arubik.craftengine.script.ScriptCall onEndCall  = null;
    @Nullable private dev.arubik.craftengine.script.ScriptCall onLoopCall = null;

    private final TreeMap<Integer, List<SoundSpec>> soundTicks = new TreeMap<>();
    private final List<ScriptAnimation> children = new ArrayList<>();

    // ===================================================================
    // BUILDER API
    // ===================================================================

    public void addKeyframe(int tick, Map<String, DisplayState> displays, Easing easing) {
        keyframes.removeIf(k -> k.tick == tick);
        keyframes.add(new Keyframe(tick, displays, easing));
        keyframes.sort(Comparator.comparingInt(k -> k.tick));
    }

    public void addKeyframe(int tick, Map<String, DisplayState> displays) {
        addKeyframe(tick, displays, Easing.LINEAR);
    }

    public void addSoundAt(int tick, SoundSpec spec) {
        soundTicks.computeIfAbsent(tick, k -> new ArrayList<>()).add(spec);
    }

    public void addChild(ScriptAnimation c) { if (!children.contains(c)) children.add(c); }
    public void removeChild(ScriptAnimation c) { children.remove(c); }

    // ===================================================================
    // PLAYBACK
    // ===================================================================

    public void play()                  { playing=true;paused=false;currentTick=0;fractionalTick=0;loopCount=0;
                                          children.forEach(ScriptAnimation::play); }
    public void playFrom(int tick)      { currentTick=Math.max(0,tick);fractionalTick=0;playing=true;paused=false;
                                          children.forEach(c -> c.playFrom(tick)); }
    public void stop()                  { playing=false;paused=false; children.forEach(ScriptAnimation::stop); }
    public void pause()                 { paused=true;  children.forEach(ScriptAnimation::pause); }
    public void resume()                { paused=false; children.forEach(ScriptAnimation::resume); }
    public void seek(int tick)          { currentTick=Math.max(0,tick);fractionalTick=0;
                                          children.forEach(c -> c.seek(tick)); }
    public void reset()                 { currentTick=0;fractionalTick=0;loopCount=0;
                                          children.forEach(ScriptAnimation::reset); }
    public void setLoop(boolean l)      { loop=l; }
    public void setMaxLoops(int max)    { maxLoops=max; }
    public void setSpeed(float s)       { speed=Math.max(0.001f,s); }
    public void setInterpolationDuration(int t){ interpolationDuration=Math.max(0,t); }
    public void setOnEndCall(@Nullable dev.arubik.craftengine.script.ScriptCall c)  { onEndCall=c; }
    public void setOnLoopCall(@Nullable dev.arubik.craftengine.script.ScriptCall c) { onLoopCall=c; }

    public boolean isPlaying()   { return playing && !paused; }
    public boolean isPaused()    { return paused; }
    public boolean isLooping()   { return loop; }
    public int currentTick()     { return currentTick; }
    public float speed()         { return speed; }
    public int duration()        { return keyframes.isEmpty() ? 0 : keyframes.get(keyframes.size()-1).tick; }
    public float progress()      { int d=duration(); return d==0?1f:Math.min(1f,(float)currentTick/d); }

    // ===================================================================
    // TICK
    // ===================================================================

    public void tickOn(net.minecraft.server.level.ServerLevel level, double ox, double oy, double oz) {
        tickOn(level, ox, oy, oz, null);
    }

    public void tickOn(net.minecraft.server.level.ServerLevel level, double ox, double oy, double oz,
                       @Nullable ScriptContext ctx) {
        if (!playing || paused || keyframes.isEmpty()) return;

        int prevTick = currentTick;
        fractionalTick += speed;
        while (fractionalTick >= 1f) { fractionalTick -= 1f; currentTick++; }

        fireSoundEvents(level, ox, oy, oz, prevTick, currentTick);

        int maxTick = duration();
        if (currentTick > maxTick) {
            if (loop && (maxLoops < 0 || loopCount < maxLoops - 1)) {
                loopCount++;
                currentTick = currentTick % Math.max(1, maxTick);
                fractionalTick = 0f;
                if (onLoopCall != null && ctx != null) {
                    try { onLoopCall.execute(ctx); } catch (Throwable ignored) {}
                }
            } else {
                playing = false;
                applyDisplays(level, ox, oy, oz, interpolate(maxTick, 0f), ctx);
                clearEntities(level);
                children.forEach(c -> { c.stop(); c.clearEntities(level); });
                if (onEndCall != null && ctx != null) {
                    try { onEndCall.execute(ctx); } catch (Throwable ignored) {}
                }
                return;
            }
        }

        applyDisplays(level, ox, oy, oz, interpolate(currentTick, fractionalTick), ctx);
        children.forEach(c -> c.tickOn(level, ox, oy, oz, ctx));
    }

    // ===================================================================
    // INTERPOLATION
    // ===================================================================

    private Map<String, DisplayState> interpolate(int tick, float frac) {
        if (keyframes.isEmpty()) return Map.of();
        Keyframe prev = keyframes.get(0), next = null;
        for (Keyframe kf : keyframes) {
            if (kf.tick <= tick) prev = kf;
            else { next = kf; break; }
        }
        if (next == null) return prev.displays;
        int span = next.tick - prev.tick;
        float rawT = span == 0 ? 1f : (tick - prev.tick + frac) / (float) span;
        float t = next.easing.apply(rawT);

        Map<String, DisplayState> result = new LinkedHashMap<>();
        Set<String> ids = new LinkedHashSet<>(prev.displays.keySet());
        ids.addAll(next.displays.keySet());
        for (String id : ids) {
            DisplayState a = prev.displays.get(id), b = next.displays.get(id);
            if (a != null && b != null) result.put(id, a.lerp(b, t));
            else if (a != null)         result.put(id, a);
            else                        result.put(id, b);
        }
        return result;
    }

    // ===================================================================
    // APPLY DISPLAYS
    // ===================================================================

    private void applyDisplays(net.minecraft.server.level.ServerLevel level,
                                double ox, double oy, double oz,
                                Map<String, DisplayState> states,
                                @Nullable ScriptContext ctx) {
        org.bukkit.World world = level.getWorld();

        for (Map.Entry<String, DisplayState> entry : states.entrySet()) {
            String id = entry.getKey();
            DisplayState s = entry.getValue();
            double wx = ox + s.px, wy = oy + s.py, wz = oz + s.pz;

            switch (s.type) {
                case "bettermodel" -> applyBetterModel(id, s, world, wx, wy, wz, ctx);
                case "modelengine" -> applyModelEngine(id, s, world, wx, wy, wz, ctx);
                case "particle"    -> applyParticle(s, world, wx, wy, wz);
                default            -> applyDisplayEntity(level, id, s, wx, wy, wz, ctx);
            }
        }

        // Remove stale display entities
        Iterator<Map.Entry<String, UUID>> it = entities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UUID> e = it.next();
            if (!states.containsKey(e.getKey())) {
                net.minecraft.world.entity.Entity ent = level.getEntity(e.getValue());
                if (ent != null) ent.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
                it.remove();
            }
        }

        // Close model renderers for ids no longer present
        bmRenderers.entrySet().removeIf(e -> {
            if (!states.containsKey(e.getKey())) { e.getValue().close(); return true; }
            return false;
        });
        meRenderers.entrySet().removeIf(e -> {
            if (!states.containsKey(e.getKey())) { e.getValue().close(); return true; }
            return false;
        });
    }

    // ---- BetterModel ----

    private void applyBetterModel(String id, DisplayState s, org.bukkit.World world,
                                   double wx, double wy, double wz,
                                   @Nullable ScriptContext ctx) {
        try {
            if (!BetterModelMachineRenderer.available() || s.modelId == null) return;
            String resolvedModel = evalExpr(s.modelId, ctx, s.modelId);
            float spd = s.modelSpeed;
            BetterModelMachineRenderer r = bmRenderers.computeIfAbsent(id,
                k -> new BetterModelMachineRenderer(resolvedModel, () -> spd));
            r.setLocation(world, wx, wy, wz, s.yaw);
            r.show();
            String anim = s.modelAnimation != null ? evalExpr(s.modelAnimation, ctx, s.modelAnimation) : null;
            if (anim != null) r.playLoop(anim);
            else r.stopAnim();
        } catch (Throwable ignored) {}
    }

    // ---- ModelEngine ----

    private void applyModelEngine(String id, DisplayState s, org.bukkit.World world,
                                   double wx, double wy, double wz,
                                   @Nullable ScriptContext ctx) {
        try {
            if (!ModelEngineMachineRenderer.available() || s.modelId == null) return;
            String resolvedModel = evalExpr(s.modelId, ctx, s.modelId);
            float spd = s.modelSpeed;
            ModelEngineMachineRenderer r = meRenderers.computeIfAbsent(id,
                k -> new ModelEngineMachineRenderer(resolvedModel, () -> spd));
            r.setLocation(world, wx, wy, wz, s.yaw);
            r.show();
            String anim = s.modelAnimation != null ? evalExpr(s.modelAnimation, ctx, s.modelAnimation) : null;
            if (anim != null) r.playLoop(anim);
            else r.stopAnim();
        } catch (Throwable ignored) {}
    }

    // ---- Particle ----

    private static void applyParticle(DisplayState s, org.bukkit.World world,
                                       double wx, double wy, double wz) {
        if (s.particleType == null || s.particleCount <= 0 || world == null) return;
        try {
            String ptName = s.particleType.toUpperCase(Locale.ROOT).replace(":", "_").replace("MINECRAFT_", "");
            org.bukkit.Particle pt = org.bukkit.Particle.valueOf(ptName);
            ParticleUtils.Shape shape = ParticleUtils.Shape.fromName(s.particleShape, ParticleUtils.Shape.POINT);
            ParticleUtils.Direction dir = (s.particleDirX != 0 || s.particleDirY != 0 || s.particleDirZ != 0)
                ? ParticleUtils.Direction.CUSTOM : ParticleUtils.Direction.RANDOM;
            ParticleUtils.emit(world, pt, wx, wy, wz,
                s.particleSpreadX, s.particleSpreadY, s.particleSpreadZ,
                shape, dir, s.particleCount, s.particleSpeed,
                s.particleDirX, s.particleDirY, s.particleDirZ);
        } catch (Throwable ignored) {}
    }

    // ---- Standard display entities (item_display, text_display, block_display, armor_stand) ----

    private void applyDisplayEntity(net.minecraft.server.level.ServerLevel level,
                                     String id, DisplayState s,
                                     double wx, double wy, double wz,
                                     @Nullable ScriptContext ctx) {
        UUID uid = entities.get(id);
        net.minecraft.world.entity.Entity entity = null;

        if (uid != null) {
            entity = level.getEntity(uid);
            if (entity == null || entity.isRemoved()) { entities.remove(id); entity = null; }
        }

        if (entity == null) {
            entity = spawnDisplayEntity(level, wx, wy, wz, s, ctx);
            if (entity != null) entities.put(id, entity.getUUID());
        } else {
            entity.setPos(wx, wy, wz);
            applyDisplayProperties(entity, s, ctx);
        }
    }

    @Nullable
    private net.minecraft.world.entity.Entity spawnDisplayEntity(
            net.minecraft.server.level.ServerLevel level, double x, double y, double z,
            DisplayState s, @Nullable ScriptContext ctx) {
        try {
            net.minecraft.world.entity.Entity e = switch (s.type) {
                case "text_display"  -> new net.minecraft.world.entity.Display.TextDisplay(
                    net.minecraft.world.entity.EntityType.TEXT_DISPLAY, level);
                case "block_display" -> new net.minecraft.world.entity.Display.BlockDisplay(
                    net.minecraft.world.entity.EntityType.BLOCK_DISPLAY, level);
                case "armor_stand"   -> new net.minecraft.world.entity.decoration.ArmorStand(
                    net.minecraft.world.entity.EntityType.ARMOR_STAND, level);
                default              -> new net.minecraft.world.entity.Display.ItemDisplay(
                    net.minecraft.world.entity.EntityType.ITEM_DISPLAY, level);
            };
            e.setPos(x, y, z);
            e.setNoGravity(true);
            level.addFreshEntity(e);
            applyDisplayProperties(e, s, ctx);
            return e;
        } catch (Throwable ignored) { return null; }
    }

    private void applyDisplayProperties(net.minecraft.world.entity.Entity entity,
                                         DisplayState s, @Nullable ScriptContext ctx) {
        if (entity.getBukkitEntity() instanceof org.bukkit.entity.ArmorStand as) {
            applyArmorStand(as, s, ctx);
        } else if (entity.getBukkitEntity() instanceof org.bukkit.entity.Display disp) {
            applyDisplayCommon(disp, s, ctx);
        }
    }

    // ---- Display entity common props ----

    private void applyDisplayCommon(org.bukkit.entity.Display disp, DisplayState s,
                                     @Nullable ScriptContext ctx) {
        try {
            // Transformation
            Quaternionf leftRot = new Quaternionf()
                .rotateY((float) Math.toRadians(s.ry))
                .rotateX((float) Math.toRadians(s.rx))
                .rotateZ((float) Math.toRadians(s.rz));
            disp.setTransformation(new org.bukkit.util.Transformation(
                new Vector3f(s.pivotX, s.pivotY, s.pivotZ),
                leftRot, new Vector3f(s.sx, s.sy, s.sz), new Quaternionf()));

            disp.setInterpolationDuration(interpolationDuration);
            disp.setInterpolationDelay(0);

            try { disp.setBillboard(org.bukkit.entity.Display.Billboard.valueOf(s.billboard.toUpperCase(Locale.ROOT))); }
            catch (IllegalArgumentException ignored) { disp.setBillboard(org.bukkit.entity.Display.Billboard.FIXED); }

            if (s.brightnessBlock >= 0 && s.brightnessSky >= 0)
                disp.setBrightness(new org.bukkit.entity.Display.Brightness(s.brightnessBlock, s.brightnessSky));
            else disp.setBrightness(null);

            disp.setGlowing(s.glow);
            if (s.viewRange > 0)       disp.setViewRange(s.viewRange);
            if (s.shadowRadius >= 0)   disp.setShadowRadius(s.shadowRadius);
            if (s.shadowStrength >= 0) disp.setShadowStrength(s.shadowStrength);

            if (disp instanceof org.bukkit.entity.ItemDisplay id)
                applyItemDisplay(id, s, ctx);
            else if (disp instanceof org.bukkit.entity.TextDisplay td)
                applyTextDisplay(td, s, ctx);
            else if (disp instanceof org.bukkit.entity.BlockDisplay bd)
                applyBlockDisplay(bd, s, ctx);
        } catch (Throwable ignored) {}
    }

    // ---- item_display ----

    private static void applyItemDisplay(org.bukkit.entity.ItemDisplay id, DisplayState s,
                                          @Nullable ScriptContext ctx) {
        String itemId = evalExpr(s.item, ctx, s.item);
        if (itemId == null || itemId.isBlank()) return;
        try {
            org.bukkit.inventory.ItemStack bukkit = resolveItem(itemId);
            if (bukkit != null) id.setItemStack(bukkit);
        } catch (Throwable ignored) {}
    }

    // ---- text_display ----

    private static void applyTextDisplay(org.bukkit.entity.TextDisplay td, DisplayState s,
                                          @Nullable ScriptContext ctx) {
        try {
            String resolved = evalTemplate(s.text, ctx);
            net.kyori.adventure.text.Component component;
            try {
                component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(resolved);
            } catch (Throwable ignored) {
                component = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                    .legacySection().deserialize(resolved);
            }
            td.text(component);

            if (s.textOpacity >= 0) td.setTextOpacity((byte)(int)(s.textOpacity * 255));
            td.setShadowed(s.textShadow);
            td.setSeeThrough(s.textSeeThrough);
            if (s.textBgColor >= 0) {
                int a=(s.textBgColor>>24)&0xFF,r=(s.textBgColor>>16)&0xFF,
                    g=(s.textBgColor>>8)&0xFF,b=s.textBgColor&0xFF;
                td.setBackgroundColor(org.bukkit.Color.fromARGB(a,r,g,b));
            }
            if (s.textLineWidth > 0) td.setLineWidth(s.textLineWidth);
            try {
                td.setAlignment(org.bukkit.entity.TextDisplay.TextAlignment.valueOf(
                    s.textAlignment.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {}
        } catch (Throwable ignored) {}
    }

    // ---- block_display ----

    private static void applyBlockDisplay(org.bukkit.entity.BlockDisplay bd, DisplayState s,
                                           @Nullable ScriptContext ctx) {
        if (s.blockId == null || s.blockId.isBlank()) return;
        try {
            String resolved = evalExpr(s.blockId, ctx, s.blockId);
            bd.setBlock(org.bukkit.Bukkit.createBlockData(resolved));
        } catch (Throwable ignored) {}
    }

    // ---- armor_stand ----

    private static void applyArmorStand(org.bukkit.entity.ArmorStand as, DisplayState s,
                                         @Nullable ScriptContext ctx) {
        try {
            as.setSmall(s.asSmall);
            as.setInvisible(s.asInvisible);
            as.setMarker(s.asMarker);
            as.setRotation(s.yaw, 0);

            if (s.asHeadItem != null) {
                var item = resolveItem(evalExpr(s.asHeadItem, ctx, s.asHeadItem));
                if (item != null) as.setHelmet(item);
            }
            if (s.asChestItem != null) {
                var item = resolveItem(evalExpr(s.asChestItem, ctx, s.asChestItem));
                if (item != null) as.setChestplate(item);
            }
            if (s.asLegsItem != null) {
                var item = resolveItem(evalExpr(s.asLegsItem, ctx, s.asLegsItem));
                if (item != null) as.setLeggings(item);
            }
            if (s.asFeetItem != null) {
                var item = resolveItem(evalExpr(s.asFeetItem, ctx, s.asFeetItem));
                if (item != null) as.setBoots(item);
            }
            if (s.asMainHand != null) {
                var item = resolveItem(evalExpr(s.asMainHand, ctx, s.asMainHand));
                if (item != null) as.getEquipment().setItemInMainHand(item);
            }
            if (s.asOffHand != null) {
                var item = resolveItem(evalExpr(s.asOffHand, ctx, s.asOffHand));
                if (item != null) as.getEquipment().setItemInOffHand(item);
            }
        } catch (Throwable ignored) {}
    }

    // ===================================================================
    // ITEM RESOLUTION
    // ===================================================================

    /** Resolve item by ID. Tries vanilla first, then CraftEngine custom items. */
    @Nullable
    private static org.bukkit.inventory.ItemStack resolveItem(String itemId) {
        if (itemId == null || itemId.isBlank()) return null;
        // Vanilla
        try {
            net.minecraft.resources.Identifier rid = net.minecraft.resources.Identifier.parse(
                itemId.contains(":") ? itemId : "minecraft:" + itemId);
            net.minecraft.world.item.Item nmsItem =
                (net.minecraft.world.item.Item) net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(rid);
            if (nmsItem != null && nmsItem != net.minecraft.world.item.Items.AIR) {
                return org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                    new net.minecraft.world.item.ItemStack(nmsItem));
            }
        } catch (Throwable ignored) {}
        // CraftEngine custom items
        try {
            String[] split = itemId.contains(":") ? itemId.split(":", 2) : new String[]{"craft_engine", itemId};
            net.momirealms.craftengine.core.util.Key key =
                net.momirealms.craftengine.core.util.Key.of(split[0], split[1]);
            net.momirealms.craftengine.bukkit.item.BukkitItemDefinition def =
                net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(key);
            if (def != null) return def.buildBukkitItem();
        } catch (Throwable ignored) {}
        return null;
    }

    // ===================================================================
    // EXPRESSION HELPERS
    // ===================================================================

    /** Evaluate a single expression from a field if it starts with '{'. Otherwise return the raw value. */
    @Nullable
    private static String evalExpr(String expr, @Nullable ScriptContext ctx, String fallback) {
        if (expr == null) return fallback;
        if (ctx != null && expr.startsWith("{") && expr.endsWith("}")) {
            try {
                return ScriptFormula.compile(expr.substring(1, expr.length() - 1))
                    .evaluate(ctx).asStr();
            } catch (Throwable ignored) {}
        }
        return expr;
    }

    /**
     * Evaluate a text template: replace all unescaped {@code {expr}} with the
     * evaluated result of the expression. Supports \{ as a literal brace.
     * The result is passed to MiniMessage.
     */
    private static String evalTemplate(String template, @Nullable ScriptContext ctx) {
        if (template == null) return "";
        if (ctx == null || !template.contains("{")) return template;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < template.length()) {
            int start = template.indexOf('{', i);
            if (start < 0) { sb.append(template, i, template.length()); break; }
            // escaped \{
            if (start > 0 && template.charAt(start - 1) == '\\') {
                sb.append(template, i, start - 1).append('{');
                i = start + 1;
                continue;
            }
            sb.append(template, i, start);
            int end = template.indexOf('}', start + 1);
            if (end < 0) { sb.append(template, start, template.length()); break; }
            String expr = template.substring(start + 1, end);
            try {
                sb.append(ScriptFormula.compile(expr).evaluate(ctx).asStr());
            } catch (Throwable ignored) {
                sb.append('{').append(expr).append('}');
            }
            i = end + 1;
        }
        return sb.toString();
    }

    // ===================================================================
    // SOUND
    // ===================================================================

    private void fireSoundEvents(net.minecraft.server.level.ServerLevel level,
                                  double ox, double oy, double oz,
                                  int fromTick, int toTick) {
        if (soundTicks.isEmpty() || fromTick >= toTick) return;
        try {
            soundTicks.subMap(fromTick, false, toTick, true).forEach((tick, specs) ->
                specs.forEach(spec -> {
                    try {
                        net.minecraft.resources.Identifier sid = net.minecraft.resources.Identifier.parse(
                            spec.soundId.contains(":") ? spec.soundId : "minecraft:" + spec.soundId);
                        net.minecraft.sounds.SoundEvent se =
                            net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(sid);
                        if (se != null)
                            level.playSound(null,
                                new net.minecraft.core.BlockPos((int)ox,(int)oy,(int)oz),
                                se, net.minecraft.sounds.SoundSource.BLOCKS,
                                spec.volume, spec.pitch);
                    } catch (Throwable ignored) {}
                }));
        } catch (Throwable ignored) {}
    }

    // ===================================================================
    // CLEAR
    // ===================================================================

    public void clearEntities(net.minecraft.server.level.ServerLevel level) {
        entities.values().forEach(uid -> {
            try {
                net.minecraft.world.entity.Entity e = level.getEntity(uid);
                if (e != null) e.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            } catch (Throwable ignored) {}
        });
        entities.clear();
        bmRenderers.values().forEach(r -> { try { r.close(); } catch (Throwable ignored) {} });
        bmRenderers.clear();
        meRenderers.values().forEach(r -> { try { r.close(); } catch (Throwable ignored) {} });
        meRenderers.clear();
        children.forEach(c -> c.clearEntities(level));
    }
}
