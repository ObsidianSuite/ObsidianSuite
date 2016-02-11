package MCEntityAnimator.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLargeShield extends ModelBase
{
    public ModelLargeShield()
    {
        m3 = new ModelRenderer(this, 8, 0);
        m3.addBox(4F, -1.5F, -10F, 1, 13, 7, 0F);
        m3.setRotationPoint(4F, 2F, 0F);
        m3.rotateAngleX = 0F;
        m3.rotateAngleY = 0F;
        m3.rotateAngleZ = 0F;
        m3.mirror = false;
        m4 = new ModelRenderer(this, 24, 0);
        m4.addBox(4F, -0.5F, -3F, 1, 11, 3, 0F);
        m4.setRotationPoint(4F, 2F, 0F);
        m4.rotateAngleX = 0F;
        m4.rotateAngleY = 0F;
        m4.rotateAngleZ = 0F;
        m4.mirror = false;
        m5 = new ModelRenderer(this, 32, 0);
        m5.addBox(4F, -1.5F, 0F, 1, 13, 2, 0F);
        m5.setRotationPoint(4F, 2F, 0F);
        m5.rotateAngleX = 0F;
        m5.rotateAngleY = 0F;
        m5.rotateAngleZ = 0F;
        m5.mirror = false;
        m2 = new ModelRenderer(this, 4, 0);
        m2.addBox(4F, -0.5F, -11F, 1, 11, 1, 0F);
        m2.setRotationPoint(4F, 2F, 0F);
        m2.rotateAngleX = 0F;
        m2.rotateAngleY = 0F;
        m2.rotateAngleZ = 0F;
        m2.mirror = false;
        m7 = new ModelRenderer(this, 44, 0);
        m7.addBox(4F, 1.5F, 4F, 1, 7, 2, 0F);
        m7.setRotationPoint(4F, 2F, 0F);
        m7.rotateAngleX = 0F;
        m7.rotateAngleY = 0F;
        m7.rotateAngleZ = 0F;
        m7.mirror = false;
        m6 = new ModelRenderer(this, 38, 0);
        m6.addBox(4F, -0.5F, 2F, 1, 11, 2, 0F);
        m6.setRotationPoint(4F, 2F, 0F);
        m6.rotateAngleX = 0F;
        m6.rotateAngleY = 0F;
        m6.rotateAngleZ = 0F;
        m6.mirror = false;
        m8 = new ModelRenderer(this, 50, 0);
        m8.addBox(4F, 3.5F, 6F, 1, 3, 2, 0F);
        m8.setRotationPoint(4F, 2F, 0F);
        m8.rotateAngleX = 0F;
        m8.rotateAngleY = 0F;
        m8.rotateAngleZ = 0F;
        m8.mirror = false;
        m9 = new ModelRenderer(this, 56, 0);
        m9.addBox(4F, 4.5F, 8F, 1, 1, 1, 0F);
        m9.setRotationPoint(4F, 2F, 0F);
        m9.rotateAngleX = 0F;
        m9.rotateAngleY = 0F;
        m9.rotateAngleZ = 0F;
        m9.mirror = false;
        m1r = new ModelRenderer(this, 0, 4);
        m1r.addBox(4F, 5.5F, -12F, 1, 3, 1, 0F);
        m1r.setRotationPoint(4F, 3F, 0F);
        m1r.rotateAngleX = 0F;
        m1r.rotateAngleY = 0F;
        m1r.rotateAngleZ = 0F;
        m1r.mirror = false;
        m1l = new ModelRenderer(this, 0, 0);
        m1l.addBox(4F, 0.5F, -12F, 1, 3, 1, 0F);
        m1l.setRotationPoint(4F, 2F, 0F);
        m1l.rotateAngleX = 0F;
        m1l.rotateAngleY = 0F;
        m1l.rotateAngleZ = 0F;
        m1l.mirror = false;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        m3.render(f5);
        m4.render(f5);
        m5.render(f5);
        m2.render(f5);
        m7.render(f5);
        m6.render(f5);
        m8.render(f5);
        m9.render(f5);
        m1r.render(f5);
        m1l.render(f5);
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    //fields
    public ModelRenderer m3;
    public ModelRenderer m4;
    public ModelRenderer m5;
    public ModelRenderer m2;
    public ModelRenderer m7;
    public ModelRenderer m6;
    public ModelRenderer m8;
    public ModelRenderer m9;
    public ModelRenderer m1r;
    public ModelRenderer m1l;
}
