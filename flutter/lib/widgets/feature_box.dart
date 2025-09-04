import 'package:ai_assistant/utils/pallete.dart';
import 'package:flutter/material.dart';

class FeatureBox extends StatelessWidget {

  final Color color;
  final String headerText;
  final String descText;

  const FeatureBox({
    super.key,
    required this.color,
    required this.headerText,
    required this.descText
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(
          horizontal: 35,
          vertical: 10
      ),
      decoration: BoxDecoration(
          color: color,
          borderRadius: const BorderRadius.all(
              Radius.circular(15)
          )
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: 20,
          horizontal: 15,
        ),
        child: Column(
          children: [
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                headerText,
                style: const TextStyle(
                    fontFamily: "Cera Pro",
                    color: Pallete.blackColor,
                    fontSize: 18,
                    fontWeight: FontWeight.bold
                ),
              ),
            ),
            const SizedBox(height: 5,),
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                descText,
                style: const TextStyle(
                  fontFamily: "Cera Pro",
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
