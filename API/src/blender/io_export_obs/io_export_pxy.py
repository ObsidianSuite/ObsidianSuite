# ##### BEGIN GPL LICENSE BLOCK #####
#
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 2
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software Foundation,
#  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
#
# ##### END GPL LICENSE BLOCK #####

# <pep8-80 compliant>

bl_info = {
    "name": "PXY Format",
    "author": "Joe Early (DaBigJoe)",
    "version": (1, 0, 0),
    "blender": (2, 74, 0),
    "location": "File > Import-Export",
    "description": "Export PXY files",
    "warning": "",
    "wiki_url": "",
    "support": 'COMMUNITY',
    "category": "Import-Export",
}


import bpy
from bpy_extras.io_utils import ExportHelper

class ExportPXY(bpy.types.Operator, ExportHelper):
    
    bl_idname = "export.pxy"
    bl_label = "Export PXY"

    filename_ext = ".pxy"

    def execute(self, context):
        file = open(self.filepath, 'w')

        objects = bpy.data.objects

        #X in Techne = X in Blender
        #Y in Techne = -Z in Blender
        #Z in Techne = Y in Blender

        for obj in objects:
            if obj.type == 'MESH':   
                file.write(obj.name.lower() + '\n')

                locX = obj.location.x
                locY = -obj.location.z
                locZ = obj.location.y

            #  sclX = obj.scale.x
            #   sclY = obj.scale.z
            #    sclZ = obj.scale.y
            #
            #    minX = obj.data.vertices[0].co.x
            #    minY = obj.data.vertices[0].co.y
            #    maxZ = obj.data.vertices[0].co.z
            #    #look for the vertex with the least X, the least Y
            #   #and greatest Z
            #    for i in range(1, 8):
            #        vrtX = obj.data.vertices[i].co.x
            #        vrtY = obj.data.vertices[i].co.y
            #        vrtZ = obj.data.vertices[i].co.z
            #        if(vrtX < minX):
            #            minX = vrtX
            #        if(vrtY < minY):
            #            minY = vrtY
            #        if(vrtZ > maxZ):
            #            maxZ = vrtZ

            #    offX = sclX*minX
            #    offY = sclY*-maxZ
            #    offZ = sclZ*minY

           #     dimX = int(round(obj.dimensions.x, 0))
           #     if dimX == 0:
           #         dimX = 1
           #     dimY = int(round(obj.dimensions.z, 0))
           #     if dimY == 0:
           #         dimY = 1
           #     dimZ = int(round(obj.dimensions.y, 0))
           #     if dimZ == 0:
           #         dimZ = 1

                #Swap?
                rotX = obj.rotation_euler.x
                rotY = obj.rotation_euler.z
                rotZ = -obj.rotation_euler.y
                
            #   file.write(str(round(offX, 2)) + ", ")
            #   file.write(str(round(offY, 2)) + ", ")
            #   file.write(str(round(offZ, 2)) + ", ")
            #   file.write(str(dimX) + ", ")
            #   file.write(str(dimY) + ", ")
            #   file.write(str(dimZ) + "\n")
                file.write(str(round(locX, 2)) + ", ")
                file.write(str(round(locY, 2)) + ", ")
                file.write(str(round(locZ, 2)) + "\n")
                file.write(str(round(rotX, 2)) + ", ")
                file.write(str(round(rotY, 2)) + ", ")
                file.write(str(round(rotZ, 2)) + "\n")

        #file.write(partsList[0].name)

        file.close
        return {'FINISHED'}

def menu_func(self, context):
    self.layout.operator(ExportPXY.bl_idname, text="PXY (.pxy)")


def register():
    bpy.utils.register_class(ExportPXY)
    bpy.types.INFO_MT_file_export.append(menu_func)


def unregister():
    bpy.utils.unregister_module(ExportPXY)
    bpy.types.INFO_MT_file_export.remove(menu_func)


if __name__ == "__main__":
    register()
    
