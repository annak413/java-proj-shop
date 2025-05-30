<script>
function updateImage() {
  const category = document.getElementById("category").value;
  const country = document.getElementById("country").value;
  const image = document.getElementById("previewImage");

  if (!category || !country) {
    image.src = "";
    return;
  }

  // Шлях до зображення (можете змінити під свою структуру)
  const imagePath = `images/${category}_${country}.jpg`; // приклад: images/electronics_japan.jpg

  image.src = imagePath;
}
</script>