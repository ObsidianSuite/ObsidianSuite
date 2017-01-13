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
    "name": "Obsidian Model Format",
    "author": "DaBigJoe",
    "version": (1, 0, 0),
    "blender": (2, 74, 0),
    "location": "File > Import-Export",
    "description": "Export Obsidian Model files",
    "warning": "",
    "wiki_url": "",
    "support": 'COMMUNITY',
    "category": "Import-Export",
}

if "bpy" in locals():
    import importlib
    if "export_obj" in locals():
        importlib.reload(export_obj)


import bpy
from bpy.props import (
        BoolProperty,
        FloatProperty,
        StringProperty,
        EnumProperty,
        )
from bpy_extras.io_utils import (
        ImportHelper,
        ExportHelper,
        orientation_helper_factory,
        path_reference_mode,
        axis_conversion,
        )


IOOBJOrientationHelper = orientation_helper_factory("IOOBJOrientationHelper", axis_forward='-Z', axis_up='Y')

class ExportMCM(bpy.types.Operator, ExportHelper, IOOBJOrientationHelper):
    """Save an Obsidian Model File"""

    bl_idname = "export.obm"
    bl_label = 'Export OBM'
    bl_options = {'PRESET'}

    filename_ext = ".obm"
    filter_glob = StringProperty(
            default="*.obm;",
            options={'HIDDEN'},
            )

    # context group
    use_selection = False
    use_animation = False

    # object group
    use_mesh_modifiers = True

    # extra data group
    use_edges = True
    use_smooth_groups = False
    use_smooth_groups_bitflags = False
    use_normals = True
    use_uvs = True
    use_materials = False
    use_triangles = True
    use_nurbs = False
    use_vertex_groups = False

    # grouping group
    use_blen_objects = True
    group_by_object = False
    group_by_material = False
    keep_vertex_order = False

    global_scale = 1.0

    path_mode = path_reference_mode

    check_extension = True

    def execute(self, context):
		#Obj export
        from . import export_obj

        from mathutils import Matrix
        keywords = self.as_keywords(ignore=("axis_forward",
                                            "axis_up",
                                            "global_scale",
                                            "check_existing",
                                            "filter_glob",
                                            ))

        global_matrix = (Matrix.Scale(self.global_scale, 4) *
                         axis_conversion(to_forward=self.axis_forward,
                                         to_up=self.axis_up,
                                         ).to_4x4())

        keywords["global_matrix"] = global_matrix
        export_obj.save(self, context, **keywords)
		
	#Setup export
        file = open(self.filepath, 'a')
        file.write("# Part #\n")
		
        objects = bpy.data.objects

        for obj in objects:
            if obj.type == 'MESH':   
                file.write(obj.name.lower() + '\n')

                locX = -obj.location.x
                locY = -obj.location.z
                locZ = obj.location.y

                rotX = obj.rotation_euler.x
                rotY = obj.rotation_euler.z
                rotZ = -obj.rotation_euler.y
                
                file.write(str(round(locX, 2)) + ", ")
                file.write(str(round(locY, 2)) + ", ")
                file.write(str(round(locZ, 2)) + "\n")
                file.write(str(round(rotX, 2)) + ", ")
                file.write(str(round(rotY, 2)) + ", ")
                file.write(str(round(rotZ, 2)) + "\n")

        file.close
        return {'FINISHED'}

def menu_func(self, context):
    self.layout.operator(ExportMCM.bl_idname, text="OBM (.obm)")


def register():
    bpy.utils.register_class(ExportMCM)
    bpy.types.INFO_MT_file_export.append(menu_func)


def unregister():
    bpy.utils.unregister_module(ExportMCM)
    bpy.types.INFO_MT_file_export.remove(menu_func)


if __name__ == "__main__":
    register()
    
