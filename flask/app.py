import os
import pandas as pd
import requests
import base64
import cv2
import math
import face_detect
import kMeansImgPy
import numpy as np
import warnings
from sklearn.cluster import KMeans
from flask import Flask, jsonify, send_from_directory, request

age = None
gender = None

app = Flask(__name__)

# Load CSV data into Pandas DataFrames
data = pd.read_csv('apparel_data2.csv', delimiter=',')
image_data = pd.read_csv('images.csv', delimiter=',')  # Load the images.csv file

# Remove rows with missing productDisplayName values
data = data.dropna(subset=['productDisplayName'])

# Directory where images are stored
image_dir = 'images'

# Transform data to JSON
def transform_to_json(data, image_data, page, items_per_page):
    start_idx = (page - 1) * items_per_page
    end_idx = start_idx + items_per_page
    
    total_results = len(data)
    total_pages = (total_results + items_per_page - 1) // items_per_page

    selected_data = data.sample(frac=1)  # Randomize the data
    selected_data = selected_data.iloc[start_idx:end_idx]
    
    # print(image_data.head())

    json_data = {
        "total_results": total_results,
        "total_pages": total_pages,
        "page": page,
        "results": []
    }
    
    for index, row in selected_data.iterrows():
        product_id = row['id']
        image_filename_row = image_data[image_data['filename'].str.contains(str(product_id))]
        
        if not image_filename_row.empty:
            image_link = image_filename_row.iloc[0]['link']
            
            image_item = {
                "image_url": image_link,
                "gender": row['gender'],
                "masterCategory": row['masterCategory'],
                "subCategory": row['subCategory'],
                "articleType": row['articleType'],
                "baseColour": row['baseColour'],
                "season": row['season'],
                "year": int(row['year']),
                "usage": row['usage'],
                "productDisplayName": row['productDisplayName'],
            }
            
            json_data["results"].append(image_item)

    return json_data

# Route to serve the JSON data with paging
@app.route('/get_data', methods=['GET'])
def get_data():
    page = int(request.args.get('page', 1))
    items_per_page = 15
    json_data = transform_to_json(data, image_data, page, items_per_page)
    return jsonify(json_data)

# Route to receive and save profile image URL
@app.route('/upload_profile_image', methods=['POST'])
def upload_profile_image():
    try:
        data = request.get_json()
        image_url = data.get('imageURL')

        # Download the image from the URL
        response = requests.get(image_url)
        response.raise_for_status()

        # Save the downloaded image as 'user_Img.jpg' in the 'profile' directory
        profile_directory = os.path.join('profile', 'user_Img.jpg')

        with open(profile_directory, 'wb') as f:
            f.write(response.content)


        return jsonify({
            "message": "Image downloaded and saved successfully.",
        })

    except Exception as e:
        return jsonify({"error": str(e)})


# Directory where try-on images will be stored
try_on_folder = 'Try'

@app.route('/send_try_on_image', methods=['POST'])
def send_try_on_image():
    try:
        data = request.get_json()
        image_url = data.get('imageUrl')  # Use 'imageUrl' instead of 'imageURL'

        # Download the image from the URL
        response = requests.get(image_url)
        response.raise_for_status()

        # Save the downloaded image as 'user_Img.jpg' in the 'profile' directory
        Try_directory = os.path.join('Try', 'tryOnRequest.jpg')

        with open(Try_directory, 'wb') as f:
            f.write(response.content)

        return jsonify({"message": "Image downloaded and saved successfully."})
    except Exception as e:
        return jsonify({"error": str(e)})
    
# Directory where the processed images are stored
processed_try_on_dir = 'ProcessedTryOn'

@app.route('/get_Tried_Outfit', methods=['GET'])
def send_image():
    try:
        image_path = os.path.join(processed_try_on_dir, 'plot.png')
        with open(image_path, 'rb') as f:
            image_data = f.read()

        # Encode the image data in base64
        base64_image = base64.b64encode(image_data).decode('utf-8')

        return jsonify({"image": base64_image})
    except Exception as e:
        return jsonify({"error": str(e)}), 500


# Route to serve images
@app.route('/images/<path:filename>')
def serve_image(filename):
    return send_from_directory(image_dir, filename)



#######################################     Age Detection      ########################################





#######################################################################################################




def calculate_rgb_range(colorsList, target_color):
    # Calculate the difference between the extracted color and the target color
    diff_red = abs(colorsList[0] - target_color[0])
    diff_green = abs(colorsList[1] - target_color[1])
    diff_blue = abs(colorsList[2] - target_color[2])
    total_diff = diff_red + diff_green + diff_blue
    return total_diff



def cluster_skin_color(colorsList):
    # Define the cluster centroids for skin tone categories


    cluster_centroids = np.array([
        [253, 231, 214],  # white
        [235, 181, 123],  # light
        [193, 152, 111],  # medium
        [146, 100, 57]    # dark
    ])
    
    # Calculate distances between colorsList and cluster centroids
    distances = np.linalg.norm(cluster_centroids - colorsList, axis=1)
    
    # Get the index of the closest cluster centroid
    closest_cluster_label = np.argmin(distances)
    
    return closest_cluster_label


def allotSkin(colorsList):
    skin_tone_categories = {
        "white": [253, 231, 214],
        "light": [235, 181, 123],
        "medium": [193, 152, 111],
        "dark": [146, 100, 57]
    }

    closest_cluster_label = cluster_skin_color(colorsList)
    closest_cluster_color = skin_tone_categories[list(skin_tone_categories.keys())[closest_cluster_label]]

    min_diff = float('inf')
    alloted_skin_tone = None

    for category, target_color in skin_tone_categories.items():
        diff = calculate_rgb_range(closest_cluster_color, target_color)
        if diff < min_diff:
            min_diff = diff
            alloted_skin_tone = category

    return alloted_skin_tone


def recommend_apparels(user_skin_tone_category, apparels_data):
    # Define the base color mappings for each skin tone category
    color_mappings = {
        "white": ["khakhi", "peach", "teal", "red", "navy blue", "blue", "fluorescent green", "green", "lime green", "sea green", "rust", "white", "black", "maroon", "magenta", "nude", "mauve", "pink", "rose", "beige", "silver", "taupe"],
        "light": ["turquoise blue", "pink", "lavender", "yellow", "skin", "beige", "grey", "grey melange", "burgundy", "gold", "white", "black", "mustard", "magenta", "nude", "mauve", "pink", "bronze", "copper", "beige", "silver", "peach"],
        "medium": ["brown", "mushroom brown", "coffee brown", "orange", "yellow", "olive", "sea green", "red", "purple", "rust", "gold", "white", "black", "maroon", "steel", "mustard", "nude", "mauve", "bronze", "copper", "off white", "silver"],
        "dark": ["black", "charcoal", "navy blue", "green", "sea green", "purple", "burgundy", "white", "tan", "nude", "cream", "pink", "rose", "metallic", "beige", "offwhite", "silver", "taupe"]
    }
    
    # Convert all base colors in CSV to lowercase
    apparels_data_lower = []
    for apparel in apparels_data:
        lower_base_colors = [color.lower().strip() for color in apparel["baseColour"].split(',')] if isinstance(apparel["baseColour"], str) else []
        apparel["baseColour"] = lower_base_colors
        apparels_data_lower.append(apparel)
    
    # Get the suitable base colors for the user's skin tone category
    suitable_colors = color_mappings.get(user_skin_tone_category.lower(), [])
    
    # Recommend apparels based on suitable colors
    recommended_apparels = []
    for apparel in apparels_data_lower:
        base_colors = apparel["baseColour"]
        if any(color in suitable_colors for color in base_colors):
            recommended_apparels.append(apparel)
            if len(recommended_apparels) >= 500:  # Limit to top 5
                break
    
    return recommended_apparels


image_path = os.path.join('profile', 'user_Img.jpg')

# Load the image from the specified path
image = cv2.imread(image_path)

# Detect face and extract
face_extracted = face_detect.detect_face(image)
# Pass extracted face to kMeans and get Max color list 
colorsList = kMeansImgPy.kMeansImage(face_extracted)

# print("Main File : ")
# print("Red Component : "+str(colorsList[0]))
# print("Green Component : "+str(colorsList[1]))
# print("Blue Component : "+str(colorsList[2]))

# Allot the actual skinTone to a certain shade
alloted_skin_tone = allotSkin(colorsList)
print("Alloted skin tone : ")
print(alloted_skin_tone)

# Load CSV data into a DataFrame
apparels_df = pd.read_csv("apparel_data2.csv")

# Example usage
user_skin_tone = alloted_skin_tone
recommended_apparels = recommend_apparels(user_skin_tone, apparels_df.to_dict('records'))

# Display recommended apparels
# print("\nRecommended Apparels:")
# for apparel in recommended_apparels:
#     print(apparel["productDisplayName"])



@app.route('/get_recommended_outfits', methods=['GET'])
def get_recommended_outfits():
    # Example usage
    user_skin_tone = alloted_skin_tone
    recommended_apparels = recommend_apparels(user_skin_tone, apparels_df.to_dict('records'))
    
    # Extract apparel IDs from the recommended_apparels
    recommended_ids = [apparel["id"] for apparel in recommended_apparels]
    
    # Filter the data based on the recommended IDs
    filtered_data = data[data['id'].isin(recommended_ids)]

    # query = request.args.get('query')
    usage = request.args.get('usage')
    season = request.args.get('season')
    sub_category = request.args.get('subCategory')




    # # Not Done: Get gender from application
    # gender = 'Women';



     # Read gender from gender.txt
    with open('gender.txt', 'r') as gender_file:
        gender = gender_file.read().strip()



    # filtered_data = data[data['productDisplayName'].str.lower().str.contains(query)]
    
    if usage:
        filtered_data = filtered_data[filtered_data['usage'] == usage]



    # For Gender Based filtering
    print(gender)
    if gender:
        filtered_data = filtered_data[filtered_data['gender'] == gender]






    if season:
        filtered_data = filtered_data[filtered_data['season'] == season]
    
    if sub_category:
        filtered_data = filtered_data[filtered_data['subCategory'] == sub_category]
    
    # Pagination parameters
    page = int(request.args.get('page', 1))
    items_per_page = 50
    
    # # Calculate start and end indices for pagination
    start_idx = (page - 1) * items_per_page
    end_idx = start_idx + items_per_page
    
    # Slice the data for the current page
    selected_data_page = filtered_data.iloc[start_idx:end_idx]
    
    # Transform data to JSON format
    # json_data = transform_to_json(selected_data_page, image_data, page, len(selected_data_page))


    json_data = transform_to_json(filtered_data, image_data, page, len(selected_data_page))

    return jsonify(json_data)










@app.route('/upload', methods=['POST'])
def upload_data():
    data = request.json
    age = data.get('Age')
    gender = data.get('Gender')
    image_url = data.get('ImageUrl')

    
    # Store the age and gender in separate text files
    with open('age.txt', 'w') as age_file:
        age_file.write(str(age))
    with open('gender.txt', 'w') as gender_file:
        gender_file.write(str(gender))
    

    print("Age:", age)
    print("Gender:", gender)

    # Download and save the image
    if image_url:
        response = requests.get(image_url)
        if response.status_code == 200:
            with open('profile/user_Img.jpg', 'wb') as f:
                f.write(response.content)
            print("Image downloaded and saved")
        else:
            print("Image download failed")

    return jsonify({"message": "Data received successfully"})













# Route to search for products based on productDisplayName
@app.route('/search', methods=['GET'])
def search_products():
    query = request.args.get('query')
    usage = request.args.get('usage')
    season = request.args.get('season')
    sub_category = request.args.get('subCategory')

    filtered_data = data[data['productDisplayName'].str.lower().str.contains(query)]
    
    if usage:
        filtered_data = filtered_data[filtered_data['usage'] == usage]
    
    if season:
        filtered_data = filtered_data[filtered_data['season'] == season]
    
    if sub_category:
        filtered_data = filtered_data[filtered_data['subCategory'] == sub_category]

    page = int(request.args.get('page', 1))
    items_per_page = 6
    json_data = transform_to_json(filtered_data, image_data, page, items_per_page)
    return jsonify(json_data)







if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)